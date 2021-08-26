package dev.triumphteam.contest

import dev.triumphteam.contest.func.tokenFromFlag
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.jda.jda
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File

fun main(args: Array<String>) {
    val token = tokenFromFlag(args)

    jda(
        module = JdaApplication::module,
        token = token,
        intents = listOf(
            GatewayIntent.GUILD_EMOJIS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MESSAGES,
        ),
        applicationFolder = File("data"),
    ) {
        disableCache(CacheFlag.ACTIVITY)
    }
}