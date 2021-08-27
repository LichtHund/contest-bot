package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun GuildMessageReceivedEvent.handleParticipants() {
    transaction {
        val data = Participants.selectAll().associate {
            val members = mutableListOf<Member?>().apply {
                add(guild.getMemberById(it[Participants.leader]))
                add(it[Participants.partner]?.let { id -> guild.getMemberById(id) })
            }.filterNotNull().joinToString(", ") { member -> member.asMention }

            it[Participants.repo] to members
        }

        val repos = data.keys.joinToString("\n") {
            if (it.length <= 25) return@joinToString it
            val modified = it.substring(0, 25)
            "[$modified...]($it)"
        }
        val members = data.values.joinToString("\n")

        val embed = embed {
            setColor(BotColor.INFO.color)
            setTitle("Participants")
            addField("Repositories", repos, true)
            addField("Members", members, true)
        }

        message.replyEmbeds(embed).mentionRepliedUser(false).queue()
    }
}
