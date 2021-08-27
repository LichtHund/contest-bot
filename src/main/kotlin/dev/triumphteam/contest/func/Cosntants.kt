package dev.triumphteam.contest.func

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

const val PARTICIPATE_COMMAND = "/participate <repo> [@partner]"

val BUTTONS = mutableMapOf(
    "cyberpunk" to ActionRow.of(Button.secondary("cyberpunk", "Cyberpunk")),
    "horror" to ActionRow.of(Button.secondary("horror", "Horror")),
    "mc2" to ActionRow.of(Button.secondary("mc2", "Minecraft 2.0")),
)