package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun GuildMessageReceivedEvent.handleVotes() {
    val votes = transaction {
        val voteResult = Votes.slice(Votes.vote, Votes.vote.count()).selectAll().groupBy(Votes.id).execute(this)
            ?: return@transaction emptyMap()
        return@transaction mutableMapOf<String, Int>().apply {
            while (voteResult.next()) {
                put(voteResult.getString(1), voteResult.getInt(2))
            }
        }
    }

    channel.sendMessageEmbeds(
        embed {
            setColor(BotColor.INFO.color)
            setTitle("Current vote status")
            setDescription(
                """
                    **Cyberpunk**: ${votes["cyberpunk"] ?: 0}
                    **Horror**: ${votes["horror"] ?: 0}
                    **Minecraft 2.0**: ${votes["mc2"] ?: 0}
                """.trimIndent()
            )
        }
    ).queue()
}