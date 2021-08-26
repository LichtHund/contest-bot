package dev.triumphteam.contest.commands

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.database.Invites
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.inBotChannel
import dev.triumphteam.contest.func.queueReply
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.team() {
    val config = feature(Config)

    // Handling commands
    on<SlashCommandEvent> {
        if (name != "team") return@on
        deferReply(true).queue()

        if (!inBotChannel(config)) return@on
        val target = getOption("member")?.asMember ?: member ?: run {
            queueReply("Could not find target for the command!")
            return@on
        }

        transaction {
            val team = Participants
                .select { Participants.leader eq target.idLong or (Participants.partner eq target.idLong) }
                .firstOrNull() ?: run {
                queueReply(
                    embed {
                        setColor(BotColor.FAIL.color)
                        setDescription("${target.asMention} is not participating in the contest!")
                    }
                )
                return@transaction
            }

            val members = mutableListOf<User?>().apply {
                add(jda.getUserById(team[Participants.leader]))
                add(team[Participants.partner]?.let { jda.getUserById(it) })
            }.filterNotNull().joinToString(", ") { it.asMention }

            queueReply(
                embed {
                    setColor(BotColor.SUCCESS.color)
                    setTitle("Team info!")
                    addField("Members", members, false)
                    addField("Repo", team[Participants.repo], false)
                }
            )
        }
    }
}