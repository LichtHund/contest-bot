package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.leader
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun GuildMessageReceivedEvent.handleKick(partner: String) {
    val partnerId = partner.toLongOrNull() ?: return
    val team = transaction {
        Participants.update({ Participants.partner eq partnerId }) {
            it[Participants.partner] = null
        }
    }

    if (team == 0) {
        message.replyEmbeds(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("Could not find team for the specified user.")
            }
        ).mentionRepliedUser(false).queue()
        return
    }

    message.replyEmbeds(
        embed {
            setColor(BotColor.SUCCESS.color)
            setDescription("User kicked from team successfully!")
        }
    ).mentionRepliedUser(false).queue()
}