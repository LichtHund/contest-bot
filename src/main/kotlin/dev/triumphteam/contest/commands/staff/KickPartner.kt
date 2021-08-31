package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Invites.team
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.leader
import dev.triumphteam.contest.database.Participants.partner
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun GuildMessageReceivedEvent.handleKick(config: Config, user: String) {
    val userId = user.toLongOrNull() ?: return

    val leaderTeam = transaction {
        Participants.select { partner eq userId or (leader eq userId) }.firstOrNull()
    }

    if (leaderTeam == null) {
        message.replyEmbeds(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("Could not find team for ${guild.getMemberById(userId)?.asMention}.")
            }
        ).mentionRepliedUser(false).queue()
        return
    }

    val leaderMember = guild.getMemberById(leaderTeam[leader])
    val partnerMember = guild.getMemberById(leaderTeam[partner] ?: 0)

    if (leaderTeam[partner] == null) {
        message.replyEmbeds(
            embed {
                setColor(BotColor.FAIL.color)
                setDescription("${leaderMember?.asMention} does not have a partner.")
            }
        ).mentionRepliedUser(false).queue()
        return
    }

    transaction {
        Participants.update({ partner eq userId or (leader eq userId) }) {
            it[partner] = null
        }
    }

    guild.getRoleById(config[Settings.ROLES].participant)?.let {
        if (partnerMember != null) {
            guild.removeRoleFromMember(partnerMember, it).queue()
        }
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
            setDescription("${member?.asMention} kicked ${partnerMember?.asMention} from ${leaderMember?.asMention}'s team.")
        }
    )?.queue()
}