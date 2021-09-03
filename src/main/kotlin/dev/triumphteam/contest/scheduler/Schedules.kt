package dev.triumphteam.contest.scheduler

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.entities.Guild
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun JdaApplication.endVoting(guild: Guild) {
    val config = feature(Config)
    closeVote(guild, config)
}

fun closeVote(guild: Guild, config: Config) {
    if (LocalDate.now() != LocalDate.of(2021, 9, 3)) return
    val votesData = config[Settings.VOTES]

    if (votesData.closed) return
    config[Settings.VOTES].closed = true
    config.save()

    val announcements = guild.getTextChannelById(votesData.votesChannel)

    announcements
        ?.retrieveMessageById(votesData.votesMessage)
        ?.complete()
        ?.editMessageComponents()
        ?.queue()

    announcements?.sendMessage(closeAnnouncement())?.queue()
}

private fun closeAnnouncement(): String {
    val votes = transaction {
        val voteResult = Votes.slice(Votes.vote, Votes.vote.count()).selectAll().groupBy(Votes.vote).execute(this)
            ?: return@transaction emptyList()
        return@transaction mutableMapOf<String, Int>().apply {
            while (voteResult.next()) {
                put(mapTheme(voteResult.getString(1)), voteResult.getInt(2))
            }
        }.toList().sortedByDescending { (_, value) -> value }
    }

    val (first, _, _) = votes

    // Idk why but if it's not this ugly and all the way to the left, it's all fucked
    return """
Hey everyone, the first **HelpChat Plugin Jam** has finally begun!
You will have until **12:00PM (London GMT+1) on September 17th** to finish your plugin!

The winning theme is: 
:white_small_square: **${first.first}** :white_small_square:

The theme voting results are:
${votes.joinToString("\n") { "**${it.first}**: ${it.second}" }}

If you haven't signed up yet, you have until **12:00PM (GMT+1) on Monday, September 6th** to enter!
Thank you to everyone who has voted, and good luck on the jam! We look forward to seeing what you will create!
    """.trimIndent()
}

private fun mapTheme(theme: String): String {
    return when (theme) {
        "mc2" -> "Minecraft 2.0"
        "horror" -> "Horror"
        else -> "Cyberpunk"
    }
}