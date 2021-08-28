package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.func.BUTTONS
import dev.triumphteam.contest.func.PARTICIPATE_COMMAND
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun GuildMessageReceivedEvent.handleVote(config: Config, channelId: String) {
    val announcements = guild.getTextChannelById(channelId) ?: return
    //if (config[Settings.VOTES].votesStarted) return
    message.delete().queue()

    config[Settings.VOTES].votesStarted = true
    config[Settings.VOTES].votesChannel = announcements.id

    val voteMessage = announcements.sendMessage(voteMessage(guild, config)).setActionRows(BUTTONS.values).complete()
    announcements
        .sendMessage("*\\*Minecraft 2.0 is basically any idea that should be in vanilla, in your opinion.*")
        .queue()

    config[Settings.VOTES].votesMessage = voteMessage.id
    config.save()
}

fun voteMessage(guild: Guild, config: Config): String {
    val votes = transaction {
        Votes.selectAll().count()
    }

    return """
            Hey @everyone!

            We are now opening theme voting for the first official HelpChat Plugin Jam! 
            The event will begin in a week, so be sure to get your votes in before the deadline on Friday, September 3rd. 
            Be sure to sign up by typing `$PARTICIPATE_COMMAND` in ${guild.getTextChannelById(config[Settings.CHANNELS].botCommands)?.asMention} so you don't miss out!
            Please check out the ${guild.getTextChannelById(config[Settings.CHANNELS].eventInfo)?.asMention} channel for more information on the event, rules, and rewards. 
            We look forward to seeing what the community chooses!
            
            Please choose from one of the themes below to cast your vote!
            **Total votes**: $votes
            """.trimIndent()
}