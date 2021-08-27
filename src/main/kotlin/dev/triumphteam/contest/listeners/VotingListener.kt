package dev.triumphteam.contest.listeners

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.PARTICIPATE_COMMAND
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun JdaApplication.voting() {
    val config = feature(Config)

    val buttons = mutableMapOf(
        "cyberpunk" to ActionRow.of(Button.secondary("cyberpunk", "Cyberpunk")),
        "horror" to ActionRow.of(Button.secondary("horror", "Horror")),
        "mc2" to ActionRow.of(Button.secondary("mc2", "Minecraft 2.0")),
    )

    on<GuildMessageReceivedEvent> {
        if (!message.contentDisplay.startsWith("!vote")) return@on
        if (config[Settings.STARTED]) return@on
        message.delete().queue()

        config[Settings.STARTED] = true
        config.save()

        channel.sendMessage(
            """
            Hey @everyone!

            We are now opening theme voting for the first official HelpChat Plugin Jam! 
            The event will begin in a week, so be sure to get your votes in before the deadline on Friday, September 3rd. 
            Be sure to sign up by typing `$PARTICIPATE_COMMAND` in ${guild.getTextChannelById(config[Settings.CHANNELS].botCommands)?.asMention} so you don't miss out!
            Please check out the ${guild.getTextChannelById(config[Settings.CHANNELS].eventInfo)?.asMention} channel for more information on the event, rules, and rewards. 
            We look forward to seeing what the community chooses!
            
            Please choose from one of the themes below to cast your vote!
            """.trimIndent()
        ).setActionRows(buttons.values).queue()
        channel
            .sendMessage("*\\*Minecraft 2.0 is basically any idea that should be in vanilla, in your opinion.*")
            .queue()
    }

    on<ButtonClickEvent> {
        val buttonId = button?.id ?: return@on
        if (buttonId !in buttons.keys) return@on
        val voter = member ?: return@on

        deferReply(true).queue()

        transaction {
            val vote = Votes.select { Votes.voter eq voter.idLong }.firstOrNull()

            if (vote == null) {
                Votes.insert {
                    it[Votes.vote] = buttonId
                    it[Votes.voter] = voter.idLong
                }

                hook.sendMessageEmbeds(
                    embed {
                        setColor(BotColor.SUCCESS.color)
                        setTitle("Thank you for voting!")
                        setDescription("You have voted for `${button?.label}`!")
                    }
                ).setEphemeral(true).queue()
                return@transaction
            }

            Votes.update({ Votes.voter eq voter.idLong }) {
                it[Votes.vote] = buttonId
            }

            hook.sendMessageEmbeds(
                embed {
                    setColor(BotColor.SUCCESS.color)
                    setTitle("Vote changed successfully!")
                    setDescription("Your vote was changed to `${button?.label}`!")
                }
            ).setEphemeral(true).queue()
        }

    }
}