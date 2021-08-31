package dev.triumphteam.contest.commands

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.repo
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.getOrNull
import dev.triumphteam.contest.func.queueReply
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.inBotChannel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalTime

private val urlPattern = "((https://)?(?<type>github|gitlab).com/(?<user>[\\w'-]+)/(?<repo>[\\w'-]+)(/)?)".toRegex()
private val scope = CoroutineScope(IO)

private val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }
        )
    }
}

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.participate() {
    val config = feature(Config)

    // Handling commands
    on<SlashCommandEvent> {
        if (name != "participate") return@on

        deferReply(true).queue()
        scope.launch {
            handleParticipate(config)
        }
    }
}

/**
 * Getting a bit complex, but will refactor for next contest
 */
private suspend fun SlashCommandEvent.handleParticipate(config: Config) {
    if (!inBotChannel(config)) return

    val member = member ?: run {
        // Should never happen
        queueReply("Member is null.")
        return
    }

    val repoUrl = getOption("repo")?.asString ?: run {
        // Should never happen
        queueReply("Could not find repo option.")
        return
    }


    val repoResult = transaction {
        Participants.select { Participants.repo eq repoUrl }.firstOrNull()
    }
    if (repoResult != null) {
        queueReply(
            embed {
                setColor(BotColor.FAIL.color)
                setTitle("Invalid repository Link!")
                setDescription("The repository is already in use.")
            }
        )
        return
    }

    val leaderResult = transaction {
        Participants.select { Participants.leader eq member.idLong or (Participants.partner eq member.idLong) }
            .firstOrNull()
    }
    if (leaderResult != null) {
        queueReply(
            embed {
                setColor(BotColor.FAIL.color)
                setTitle("You're already participating in the contest!")
                setDescription("If you believe this is a mistake please contact a staff member.")
            }
        )
        return
    }

    val (_, protocol, type, user, repo) = urlPattern.matchEntire(repoUrl)?.destructured ?: run {
        queueReply(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("Please make sure you entered a valid GitHub/GitLab link.")
            }
        )
        return
    }

    val repository = if (protocol.isEmpty()) "https://$repoUrl" else repoUrl

    val private = when (type) {
        "github" -> {
            val (private) = client.getOrNull<GitHubData>("https://api.github.com/repos/$user/$repo") ?: run {
                notPublicFail()
                return
            }

            private
        }

        "gitlab" -> {
            val (id) = client.getOrNull<GitLabData>("https://gitlab.com/api/v4/projects/$user%2f$repo") ?: run {
                notPublicFail()
                return
            }

            id == 0L
        }

        else -> true
    }

    if (private) {
        notPublicFail()
        return
    }

    val partner = getOption("partner")?.asMember

    if (partner?.idLong == member.idLong) {
        queueReply(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("You can't invite yourself!")
            }
        )
        return
    }

    if (partner?.user?.isBot == true) {
        queueReply(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("You cannot invite bots to your team!")
            }
        )
        return
    }

    val team = transaction {
        Participants.insertAndGetId {
            it[leader] = member.idLong
            it[Participants.repo] = repository
        }
    }

    val logChannel = guild?.getTextChannelById(config[Settings.CHANNELS].contestLog)

    logChannel?.sendMessageEmbeds(
        embed {
            setColor(BotColor.INFO.color)
            setTitle("New team registered.")
            addField("Leader", member.asMention, false)
            addField("Repository", repository, false)
            setTimestamp(Instant.now())
        }
    )?.queue()

    if (partner != null) {
        if (!invitePartner(config, partner, team, member, logChannel)) return
    }

    val embed = embed {
        setColor(BotColor.SUCCESS.color)
        setTitle("You're in!")
        addField("Repository", repository, false)
        addField("Leader", member.asMention, false)

        if (partner != null) {
            addField(
                "Partner",
                "An invite was sent to ${partner.asMention}.\nThey need to do `/accept <@inviter>` to accept.",
                false
            )
            return@embed
        }
    }

    guild?.getRoleById(config[Settings.ROLES].participant)?.let {
        guild?.addRoleToMember(member, it)?.queue()
    }

    queueReply(embed)
}

@Serializable
data class GitHubData(val private: Boolean)

@Serializable
data class GitLabData(val id: Long)

private fun SlashCommandEvent.notPublicFail() {
    queueReply(
        embed {
            setColor(BotColor.FAIL.color)
            setDescription("Please make sure your repository is public!")
        }
    )
}