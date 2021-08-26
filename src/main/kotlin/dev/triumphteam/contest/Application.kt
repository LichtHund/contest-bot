package dev.triumphteam.contest

import dev.triumphteam.contest.func.tokenFromFlag
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.jda.jda
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
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
        addEventListeners(
            // Had to move this here because it's called before everything else
            object : ListenerAdapter() {
                override fun onGuildReady(event: GuildReadyEvent) {
                    val guild = event.guild

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

                    //guild.updateCommands().queue()
                }
            }
        )
    }
}