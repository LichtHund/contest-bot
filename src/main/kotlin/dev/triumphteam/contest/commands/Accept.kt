package dev.triumphteam.contest.commands

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Invites
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Participants.leader
import dev.triumphteam.jda.JdaApplication
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.inBotChannel
import dev.triumphteam.contest.func.queueReply
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

/**
 * The current commands are temporary, JDA's way is really annoying
 */
fun JdaApplication.accept() {
    val config = feature(Config)

    // Handling commands
    on<SlashCommandEvent> {
        if (name != "accept") return@on
        deferReply(true).queue()

        if (!inBotChannel(config)) return@on
        val member = member ?: run {
            // Should never happen
            queueReply("Could not find member.")
            return@on
        }

        val inviter = getOption("inviter")?.asMember ?: run {
            // Should never happen
            queueReply("Could not find inviter option.")
            return@on
        }

        transaction {
            // TODO figure and fix
            val userTeam = Participants.select { leader eq member.idLong or (Participants.partner eq member.idLong) }
                .firstOrNull()

            if (userTeam != null) {
                queueReply(
                    embed {
                        setColor(BotColor.FAIL.color)
                        setTitle("You're already participating in the contest!")
                        setDescription("If you believe this is a mistake please contact a staff member.")
                    }
                )
                return@transaction
            }

            val team = Participants.select { leader eq inviter.idLong }.firstOrNull() ?: run {
                queueReply(
                    embed {
                        setColor(BotColor.FAIL.color)
                        setDescription("${inviter.asMention} is not participating!\nMake sure you typed the right user!")
                    }
                )
                return@transaction
            }

            val invite = Invites.select { Invites.team eq team[Participants.id] }.firstOrNull() ?: run {
                queueReply(
                    embed {
                        setColor(BotColor.FAIL.color)
                        setDescription("You have no pending invite by ${inviter.asMention}!")
                    }
                )
                return@transaction
            }

            Participants.update({ Participants.id eq team[Participants.id] }) {
                it[partner] = member.idLong
            }

            Invites.deleteWhere { Invites.id eq invite[Invites.id] }

            guild?.getTextChannelById(config[Settings.CHANNELS].contestLog)?.sendMessageEmbeds(
                embed {
                    setColor(BotColor.INFO.color)
                    setTitle("Member joined.")
                    setDescription("${member.asMention} has joined ${inviter.asMention}'s team.")
                    setTimestamp(Instant.now())
                }
            )?.queue()

            guild?.getRoleById(config[Settings.ROLES].participant)?.let {
                guild?.addRoleToMember(member, it)?.queue()
            }

            queueReply(
                embed {
                    setColor(BotColor.SUCCESS.color)
                    setDescription("You have joined ${inviter.asMention}'s team!\n`/team` for more information!")
                }
            )
        }
    }
}