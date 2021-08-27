package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.commands.accept
import dev.triumphteam.contest.commands.invite
import dev.triumphteam.contest.commands.participate
import dev.triumphteam.contest.commands.staff.staffCommands
import dev.triumphteam.contest.commands.team
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.database.Database
import dev.triumphteam.contest.listeners.voting
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.listen
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

fun JdaApplication.module() {
    val config = install(Config)
    install(Database)

    // Commands (I know)
    listen(JdaApplication::participate)
    listen(JdaApplication::accept)
    listen(JdaApplication::invite)
    listen(JdaApplication::team)
    // Staff commands
    listen(JdaApplication::staffCommands)
    // Actual listener
    listen(JdaApplication::voting)

    jda.presence.setPresence(Activity.competing("the Plugin Jam!"), false)
    upsertCommands()
}

fun JdaApplication.upsertCommands() {
    jda.guilds.forEach { guild ->
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
                addOption(OptionType.STRING, "repo", "GitHub repository for the contest", true)
                addOption(OptionType.USER, "partner", "Teams of 2 are allowed, so introduce your partner")
            }
        ).queue()

        guild.upsertCommand(CommandData("help", "Shows available commands")).queue()
        guild.upsertCommand(CommandData("disband", "Disbands team")).queue()

        guild.updateCommands().queue()
    }
}