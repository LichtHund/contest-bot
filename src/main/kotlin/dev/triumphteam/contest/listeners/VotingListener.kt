package dev.triumphteam.contest.listeners

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BUTTONS
import dev.triumphteam.contest.func.PARTICIPATE_COMMAND
import dev.triumphteam.contest.func.isManager
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun JdaApplication.voting() {

    on<ButtonClickEvent> {
        val buttonId = button?.id ?: return@on
        if (buttonId !in BUTTONS.keys) return@on
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