package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.commands.accept
import dev.triumphteam.contest.commands.invite
import dev.triumphteam.contest.commands.participate
import dev.triumphteam.contest.commands.team
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.database.Database
import dev.triumphteam.contest.listeners.voting
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.listen
import net.dv8tion.jda.api.entities.Activity

fun JdaApplication.module() {
    install(Config)
    install(Database)

    // Commands (I know)
    listen(JdaApplication::participate)
    listen(JdaApplication::accept)
    listen(JdaApplication::invite)
    listen(JdaApplication::team)
    // Actual listener
    listen(JdaApplication::voting)

    jda.presence.setPresence(Activity.competing("in Plugin Jam!"), false)
}