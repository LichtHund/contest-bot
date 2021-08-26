package dev.triumphteam.contest

import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.contest.commands.participate
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.database.Database
import dev.triumphteam.contest.listeners.voting
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.kipp.event.listen
import dev.triumphteam.kipp.event.on
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent

fun JdaApplication.module() {
    install(Config)
    install(Database)

    listen(JdaApplication::participate)
    listen(JdaApplication::voting)

    on<ButtonClickEvent> {
        when (button?.id) {
            "option1" -> reply("Option 1!").setEphemeral(true).queue()
            "option2" -> reply("Option 2!").setEphemeral(true).queue()
            "option3" -> reply("Option 3!").setEphemeral(true).queue()
            "option4" -> reply("Option 4!").setEphemeral(true).queue()
        }
    }

}