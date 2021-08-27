package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.leader
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun GuildMessageReceivedEvent.handleEdit(config: Config, leader: String, repo: String) {
    val leaderId = leader.toLongOrNull() ?: return
    val leaderMember = guild.getMemberById(leaderId)
    val team = transaction {
        Participants.update({ Participants.leader eq leaderId }) {
            it[Participants.repo] = repo
        }
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
            setDescription("${leaderMember?.asMention}'s team's repository changed successfully!")
        }
    ).mentionRepliedUser(false).queue()

    guild.getTextChannelById(config[Settings.CHANNELS].contestLog)?.sendMessageEmbeds(
        embed {
            setColor(BotColor.FAIL.color)
            setDescription("${member?.asMention} changed ${leaderMember?.asMention}'s team's repository.")
        }
    )
}