package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun GuildMessageReceivedEvent.handleHelp() {
    message.replyEmbeds(
        embed {
            setColor(BotColor.INFO.color)
            setTitle("Staff commands:")
            setDescription(
                """
                    `!disband <leader-id>` - Disbands the user's team.
                    `!kickpartner <partner-id|leader-id>` - Kick's a partner from a team.
                    `!editrepo <leader-id> <new-repo-url>` - Changes a teams repository.
                    `!participants` - Shows list of all participants.
                    `!votes` - Shows current vote results.
                """.trimIndent()
            )
        }
    ).mentionRepliedUser(false).queue()
}