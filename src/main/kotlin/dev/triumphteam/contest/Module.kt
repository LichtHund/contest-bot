package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.listeners.commands
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.listen
import dev.triumphteam.kipp.event.on
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle

fun JdaApplication.module() {

    listen(JdaApplication::commands)

    on<ButtonClickEvent> {
        when (button?.id) {
            "option1" -> reply("Option 1!").setEphemeral(true).queue()
            "option2" -> reply("Option 2!").setEphemeral(true).queue()
            "option3" -> reply("Option 3!").setEphemeral(true).queue()
            "option4" -> reply("Option 4!").setEphemeral(true).queue()
        }
    }

}