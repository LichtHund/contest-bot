package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.commands.accept
import dev.triumphteam.contest.commands.invite
import dev.triumphteam.contest.commands.participate
import dev.triumphteam.contest.commands.staff.participants
import dev.triumphteam.contest.commands.team
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Database
import dev.triumphteam.contest.listeners.voting
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.listen
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege

fun JdaApplication.module() {
    val config = install(Config)
    install(Database)

    // Commands (I know)
    listen(JdaApplication::participate)
    listen(JdaApplication::accept)
    listen(JdaApplication::invite)
    listen(JdaApplication::team)
    // Staff commands
    listen(JdaApplication::participants)
    // Actual listener
    listen(JdaApplication::voting)

    jda.presence.setPresence(Activity.competing("the Plugin Jam!"), false)

}