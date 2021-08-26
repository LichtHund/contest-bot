package dev.triumphteam.contest.listeners

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.getOrNull
import dev.triumphteam.contest.func.plural
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.on
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.requests.restaction.MessageAction
import org.apache.commons.validator.routines.UrlValidator
import java.awt.Color

private val urlPattern = "(https://github.com/(?<user>[\\w'-]+)/(?<repo>[\\w'-]+)(/)?)".toRegex()
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
fun JdaApplication.commands() {
    val config = feature(Config)

    // Adds guild commands
    on<GuildReadyEvent> {
        jda.deleteCommandById(880209445211217930)
        //guild.registerStart()
        guild.registerParticipate()
        guild.updateCommands().queue()
    }

    // Handling commands
    on<SlashCommandEvent> {
        when (name) {
            "participate" -> {
                deferReply(true).queue()
                scope.launch {
                    handleParticipate(config)
                }
            }
        }
    }
}

private fun Guild.registerParticipate() {
    upsertCommand(
        CommandData("participate", "Sign up for the contest").apply {
            addOption(OptionType.STRING, "repo", "GitHub repository for the contest", true)
            addOption(OptionType.USER, "partner", "Teams of 2 are allowed, so introduce your partner")
        }
    ).complete()
}

private fun Guild.registerStart() {
    upsertCommand(
        CommandData("start", "Starts the contest").apply {
            addOption(OptionType.STRING, "date", "Date for the beginning of the contest", true)
        }
    ).queue()
}

private suspend fun SlashCommandEvent.handleParticipate(config: Config) {
    val repoUrl = getOption("repo")?.asString ?: run {
        // Should never happen
        hook.sendMessage("Could not find repo option.").setEphemeral(true).queue()
        return
    }

    val (_, user, repo) = urlPattern.matchEntire(repoUrl)?.destructured ?: run {
        hook.sendMessageEmbeds(
            embed {
                setTitle("Invalid repository Link!")
                setDescription("Please make sure you entered a valid GitHub/GitLab link.")
            }
        ).setEphemeral(true).queue()
        return
    }

    val (private) = client.getOrNull<GitHubData>("https://api.github.com/repos/$user/$repo") ?: run {
        notPublicFail()
        return
    }

    if (private) {
        notPublicFail()
        return
    }

    val partner = getOption("partner")?.asMember

    val participants = mutableListOf(member?.asMention).apply {
        if (partner != null) add(partner.asMention)
    }

    val embed = embed {
        setColor(BotColor.SUCCESS.color)
        setTitle("You're in!")
        addField("Repo", "$user/$repo", false)
        addField("Members", participants.joinToString(", "), false)
    }

    hook.sendMessageEmbeds(embed).setEphemeral(true).queue()
    //replyEmbeds(embed).setEphemeral(true).queue()
    // TODO add member and handle fail
}

@Serializable
data class GitHubData(val private: Boolean)

fun SlashCommandEvent.notPublicFail() {
    hook.sendMessageEmbeds(
        embed {
            setTitle("Invalid repository!")
            setDescription("Please make sure your repository is public!")
        }
    ).setEphemeral(true).queue()
}