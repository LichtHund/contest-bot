package dev.triumphteam.contest.listeners

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.plural
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.on
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.commands() {
    val config = feature(Config)

    // Adds guild commands
    on<GuildReadyEvent> {
        jda.deleteCommandById(880209445211217930)
        //guild.registerStart()
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
    ).complete()
}

private fun Guild.registerStart() {
    upsertCommand(
        CommandData("start", "Starts the contest").apply {
            addOption(OptionType.STRING, "date", "Date for the beginning of the contest", true)
        }
    ).queue()
}

private fun SlashCommandEvent.handleParticipate() {
    deferReply(true).queue()

    val repo = getOption("repo")?.asString ?: run {
        reply("Could not find repo option.").queue()
        return
    }

    val partner = getOption("partner")?.asMember

    val participants = mutableListOf(member?.asMention).apply {
        if (partner != null) add(partner.asMention)
    }

    val embed = embed {
        setColor(BotColor.SUCCESS.color)
        setTitle("You're in!")
        addField("Repo", repo, false)
        addField("Members", participants.joinToString(", "), false)
    }

    hook.sendMessageEmbeds(embed).setEphemeral(true).queue()
    //replyEmbeds(embed).setEphemeral(true).queue()
    // TODO add member and handle fail
}

/**
 * Hard coded for now, can change later if needed
 */
private fun SlashCommandEvent.handleStart() {
    // TODO temporary
    val embed = embed {
        setColor(Color.decode("#2ecc71"))
        setTitle("Test")
        setDescription("Testing buttons with embeded")
    }

    reply("Done").setEphemeral(true).queue()

    channel.sendMessage(
        """
            Hey @everyone!

            We are now opening theme voting for the first official HelpChat Plugin Jam! The event will begin in a week, so be sure to get your votes in before the deadline on Friday, September 3rd. Be sure to sign up by typing /participate <repo> [@partner] in #bot-commands so you don't miss out! Please check out the #event-info channel for more information on the event, rules, and rewards. 
            
            We look forward to seeing what the community chooses!
        """.trimIndent()
    ).setActionRows(
        ActionRow.of(Button.secondary("cyberpunk", "Cyberpunk")),
        ActionRow.of(Button.secondary("horror", "Horror")),
        ActionRow.of(Button.secondary("mc2", "Minecraft 2.0")),
    ).queue()
}