package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Invites.team
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun JdaApplication.participants() {
    on<GuildMessageReceivedEvent> {
        if (!message.contentDisplay.startsWith('!')) return@on
        val args = message.contentDisplay.removePrefix("!").split(" ")

        when (args[0]) {
            "participants" -> {
                if (args.size > 1) return@on

                handleParticipants()
            }
        }
    }
}

fun GuildMessageReceivedEvent.handleParticipants() {
    transaction {
        val data = Participants.selectAll().associate {
            val members = mutableListOf<Member?>().apply {
                add(guild.getMemberById(it[Participants.leader]))
                add(it[Participants.partner]?.let { id -> guild.getMemberById(id) })
            }.filterNotNull().joinToString(", ") { member -> member.asMention }

            it[Participants.repo] to members
        }

        val repos = data.keys.joinToString("\n")
        val members = data.values.joinToString("\n")

        val embed = embed {
            setColor(BotColor.INFO.color)
            setTitle("Participants")
            addField("Repositories", repos, true)
            addField("Members", members, true)
        }

        channel.sendMessageEmbeds(embed).queue()
    }
}