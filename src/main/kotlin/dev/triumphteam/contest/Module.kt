package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.jda.JdaApplication
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

    on<GuildReadyEvent> {
        guild.updateCommands().queue()
        guild.upsertCommand(
            CommandData("participate", "Sign up for the contest").apply {
                addOption(OptionType.STRING, "repo", "GitHub/GitLab repository for the contest", true)
                addOption(OptionType.USER, "partner", "Teams of 2 are allowed, so introduce your partner")
            }
        ).queue()

        guild.upsertCommand(
            CommandData("start", "Start contest").apply {
                addOption(OptionType.STRING, "date", "Date for the beginning of the contest", true)
                setDefaultEnabled(false)
            }
        ).queue()
    }

    on<GuildMessageReceivedEvent> {
        if (!message.contentDisplay.startsWith("!test")) return@on
        channel.sendMessage("Test")
            .setActionRows(
                ActionRow.of(Button.secondary("option1", "option 1")),
                ActionRow.of(Button.secondary("option2", "option 2")),
                ActionRow.of(Button.secondary("option3", "option 3")),
                ActionRow.of(Button.secondary("option4", "option 4")),
            )
            .queue()
    }

    on<ButtonClickEvent> {
        when (button?.id) {
            "option1" -> reply("Option 1!").setEphemeral(true).queue()
            "option2" -> reply("Option 2!").setEphemeral(true).queue()
            "option3" -> reply("Option 3!").setEphemeral(true).queue()
            "option4" -> reply("Option 4!").setEphemeral(true).queue()
        }
    }

}