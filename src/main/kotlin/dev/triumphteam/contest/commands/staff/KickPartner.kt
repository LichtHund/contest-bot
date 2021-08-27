package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.leader
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun GuildMessageReceivedEvent.handleKick(config: Config, user: String) {
    val userId = user.toLongOrNull() ?: return

    val leaderId = transaction {
        Participants.select { Participants.partner eq userId or (leader eq userId) }.firstOrNull()
    }?.get(Participants.leader) ?: 0
    val leaderMember = guild.getMemberById(leaderId)

    val team = transaction {
        Participants.update({ Participants.partner eq userId or (leader eq userId) }) {
            it[Participants.partner] = null
        }
    }

    if (team == 0) {
        message.replyEmbeds(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("Could not find team for ${guild.getMemberById(userId)?.asMention}.")
            }
        ).mentionRepliedUser(false).queue()
        return
    }

    message.replyEmbeds(
        embed {
            setColor(BotColor.SUCCESS.color)
            setDescription("Partner kicked from ${leaderMember?.asMention}'s team successfully!")
        }
    ).mentionRepliedUser(false).queue()

    guild.getTextChannelById(config[Settings.CHANNELS].contestLog)?.sendMessageEmbeds(
        embed {
            setColor(BotColor.FAIL.color)
            setDescription("${member?.asMention} kicked ${leaderMember?.asMention}'s partner.")
        }
    )?.queue()
}