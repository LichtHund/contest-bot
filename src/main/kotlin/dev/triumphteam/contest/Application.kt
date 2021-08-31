package dev.triumphteam.contest

import dev.triumphteam.contest.func.tokenFromFlag
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.jda.jda
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.Bukkit
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
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
        ),
        applicationFolder = File("data"),
    ) {
        disableCache(CacheFlag.ACTIVITY)
    }
}