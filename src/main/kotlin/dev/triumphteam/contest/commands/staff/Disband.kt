package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

fun GuildMessageReceivedEvent.handleDisband(config: Config, leader: String) {
    val leaderId = leader.toLongOrNull() ?: return
    val leaderMember = guild.getMemberById(leaderId)
    val team = transaction {
        Participants.deleteWhere { Participants.leader eq leaderId }
    }

    if (team == 0) {
        message.replyEmbeds(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("Could not find team for ${leaderMember?.asMention}.")
            }
        ).mentionRepliedUser(false).queue()
        return
    }

    message.replyEmbeds(
        embed {
            setColor(BotColor.SUCCESS.color)
            setDescription("${leaderMember?.asMention}'s team disbanded successfully!")
        }
    ).mentionRepliedUser(false).queue()

    guild.getTextChannelById(config[Settings.CHANNELS].contestLog)?.sendMessageEmbeds(
        embed {
            setColor(BotColor.FAIL.color)
            setDescription("${member?.asMention} disbanded ${leaderMember?.asMention}'s team.")
        }
    )
}