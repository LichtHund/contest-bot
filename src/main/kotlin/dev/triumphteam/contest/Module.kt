package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.commands.accept
import dev.triumphteam.contest.commands.invite
import dev.triumphteam.contest.commands.participate
import dev.triumphteam.contest.commands.staff.pageListener
import dev.triumphteam.contest.commands.staff.staffCommands
import dev.triumphteam.contest.commands.team
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.database.Database
import dev.triumphteam.contest.event.listen
import dev.triumphteam.contest.listeners.voting
import dev.triumphteam.contest.scheduler.Scheduler
import dev.triumphteam.contest.scheduler.runTaskLater
import dev.triumphteam.contest.scheduler.seconds
import dev.triumphteam.jda.JdaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

private val scope = CoroutineScope(IO)

fun JdaApplication.module() {
    install(Config)
    install(Database)
    install(Scheduler)

    // Commands (I know)
    listen(JdaApplication::participate)
    listen(JdaApplication::accept)
    listen(JdaApplication::invite)
    listen(JdaApplication::team)
    // Staff commands
    listen(JdaApplication::staffCommands)
    // Actual listener
    listen(JdaApplication::voting)
    listen(JdaApplication::pageListener)

    jda.presence.setPresence(Activity.competing("the Plugin Jam!"), false)

    runTaskLater(seconds(5)) {
        upsertCommands()
    }

    scope.launch {
        console()
    }
}

/**
 * Just simple system for console commands for using the bot to message
 */
fun JdaApplication.console() {
    while (true) {
        val args = readLine()?.split(" ") ?: continue
        if (args.isEmpty()) continue

        when (args[0]) {
            "msg" -> {
                if (args.size < 3) {
                    println("Args short!")
                    continue
                }

                val channel = jda.getTextChannelById(args[1])
                if (channel == null) {
                    println("No channel!")
                    continue
                }

                channel.sendMessage(args.subList(2, args.size).joinToString(" ")).queue()
                println("Sent!")
            }

            "reply" -> {
                if (args.size < 4) {
                    println("Args short!")
                    continue
                }

                val channel = jda.getTextChannelById(args[1])
                if (channel == null) {
                    println("No channel!")
                    continue
                }

                try {
                    val message = channel.retrieveMessageById(args[2]).complete()
                    message.reply(args.subList(3, args.size).joinToString(" ")).queue()
                    println("Sent!")
                } catch (exception: ErrorResponseException) {
                    println("No message!")
                }
            }
        }
    }
}

fun JdaApplication.upsertCommands() {
    jda.guilds.forEach { guild ->
        //runTaskLater(MINUTES_TILL_NOON) { endVoting(guild) }

        // Upsert all commands
        guild.upsertCommand(
            CommandData("accept", "Accept a team invite").apply {
                addOption(OptionType.USER, "inviter", "Member that invited you", true)
            }
        ).queue()
        guild.upsertCommand(
            CommandData("invite", "Invite a partner").apply {
                addOption(OptionType.USER, "partner", "Partner you want to invite", true)
            }
        ).queue()
        guild.upsertCommand(
            CommandData("team", "Information about your team").apply {
                addOption(OptionType.USER, "member", "Checks the member's team information")
            }
        ).queue()
        guild.upsertCommand(
            CommandData("participate", "Sign up for the contest").apply {
                addOption(OptionType.STRING, "repo", "GitHub/Gitlab repository for the contest", true)
                addOption(OptionType.USER, "partner", "Teams of 2 are allowed, so introduce your partner")
            }
        ).queue()

        //guild.updateCommands().queue()
    }
}