package dev.triumphteam.contest.listeners

import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.plural
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.on
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.awt.Color

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.commands() {
    // Adds guild commands
    on<GuildReadyEvent> {
        guild.registerStart()
        guild.registerParticipate()
        guild.updateCommands().queue()
    }

    // Handling commands
    on<SlashCommandEvent> {
        when (name) {
            "participate" -> handleParticipate()
            "start" -> handleStart()
        }
    }
}

private fun Guild.registerParticipate() {
    upsertCommand(
        CommandData("participate", "Sign up for the contest").apply {
            addOption(OptionType.STRING, "repo", "GitHub/GitLab repository for the contest", true)
            addOption(OptionType.USER, "partner", "Teams of 2 are allowed, so introduce your partner")
        }
    ).queue()
}

private fun Guild.registerStart() {
    upsertCommand(
        CommandData("start", "Starts the contest").apply {
            addOption(OptionType.STRING, "date", "Date for the beginning of the contest", true)
        }
    ).queue()
}

private fun SlashCommandEvent.handleParticipate() {
    val repo = getOption("repo")?.asString ?: run {
        reply("Could not find repo option.").queue()
        return
    }

    val partner = getOption("partner")?.asMember

    val participants = mutableListOf(member?.asMention).apply {
        if (partner != null) add(partner.asMention)
    }

    val embed = embed {
        setColor(Color.decode("#2ecc71"))
        setTitle("You're in!")
        addField("Repo", repo, false)
        addField("Members", participants.joinToString(", "), false)
    }

    replyEmbeds(embed).setEphemeral(true).queue()
    // TODO add member and handle fail
}

private fun SlashCommandEvent.handleStart() {

}