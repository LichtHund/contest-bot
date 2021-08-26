package dev.triumphteam.contest.commands

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.on
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.accept() {
    val config = feature(Config)

    // Handling commands
    on<SlashCommandEvent> {
        if (name != "accept") return@on
        deferReply(true).queue()


    }
}