package dev.triumphteam.contest.commands.staff

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Invites.team
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BUTTONS
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.PARTICIPATE_COMMAND
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.isManager
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun JdaApplication.staffCommands() {
    val config = feature(Config)

    on<GuildMessageReceivedEvent> {
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
                handleDisband(args[1])
            }
            "kickpartner" -> {
                if (args.size != 2) return@on
                handleKick(args[1])
            }
            "editrepo" -> {
                if (args.size != 3) return@on
                handleEdit(args[1], args[2])
            }
        }
    }
}

