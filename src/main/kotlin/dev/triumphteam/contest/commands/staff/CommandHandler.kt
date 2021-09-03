package dev.triumphteam.contest.commands.staff

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.isManager
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

fun JdaApplication.staffCommands() {
    val config = feature(Config)

    on<GuildMessageReceivedEvent> {
        if (author.isBot) return@on
        if (!message.contentDisplay.startsWith('!')) return@on
        if (member?.isManager(config) != true) return@on
        val args = message.contentDisplay.removePrefix("!").split(" ")

        when (args[0]) {
            "participants" -> {
                if (args.size > 1) return@on
                handleParticipants()
            }
            "vote" -> {
                if (args.size != 2) return@on
                handleVote(config, args[1])
            }
            "votes" -> {
                if (args.size > 1) return@on
                handleVotes()
            }
            "disband" -> {
                if (args.size != 2) return@on
                handleDisband(config, args[1])
            }
            "kickpartner" -> {
                if (args.size != 2) return@on
                handleKick(config, args[1])
            }
            "editrepo" -> {
                if (args.size != 3) return@on
                handleEdit(config, args[1], args[2])
            }
            "help" -> {
                if (args.size > 1) return@on
                handleHelp()
            }
        }
    }
}


