package dev.triumphteam.contest.commands

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.inBotChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.accept() {
    val config = feature(Config)

    // Handling commands
    on<SlashCommandEvent> {
        if (name != "accept") return@on
        deferReply(true).queue()

        if (!inBotChannel(config)) return@on


    }
}