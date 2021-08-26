package dev.triumphteam.contest.listeners

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.on
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

fun JdaApplication.voting() {
    val config = feature(Config)

    on<GuildMessageReceivedEvent> {
        if (!message.contentDisplay.startsWith("!vote")) return@on
        message

        channel.sendMessage(
            """
            Hey @everyone!

            We are now opening theme voting for the first official HelpChat Plugin Jam! 
            The event will begin in a week, so be sure to get your votes in before the deadline on Friday, September 3rd. 
            Be sure to sign up by typing `/participate <repo> [@partner]` in ${guild.getTextChannelById(config[Settings.CHANNELS].botCommands)?.asMention} so you don't miss out!
            Please check out the ${guild.getTextChannelById(config[Settings.CHANNELS].eventInfo)?.asMention} channel for more information on the event, rules, and rewards. 
            We look forward to seeing what the community chooses!
            
            Please choose from one of the themes below to cast your vote!
            """.trimIndent()
        ).setActionRows(
            ActionRow.of(Button.secondary("cyberpunk", "Cyberpunk")),
            ActionRow.of(Button.secondary("horror", "Horror")),
            ActionRow.of(Button.secondary("mc2", "Minecraft 2.0")),
        ).queue()
    }

    on<ButtonClickEvent> {
        when (button?.id) {
            "cyberpunk" -> reply("clicked on cyberpunk").queue()
        }
    }
}