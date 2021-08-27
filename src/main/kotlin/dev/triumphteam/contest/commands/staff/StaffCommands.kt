package dev.triumphteam.contest.commands.staff

import dev.triumphteam.bukkit.feature.feature
import dev.triumphteam.contest.config.Config
import dev.triumphteam.contest.config.Settings
import dev.triumphteam.contest.database.Invites.team
import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.database.Votes
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BUTTONS
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.PARTICIPATE_COMMAND
import dev.triumphteam.contest.func.embed
import dev.triumphteam.contest.func.isManager
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun JdaApplication.staffCommands() {
    val config = feature(Config)

    on<GuildMessageReceivedEvent> {
        if (!message.contentDisplay.startsWith('!')) return@on
        if (member?.isManager(config) != true) return@on
        val args = message.contentDisplay.removePrefix("!").split(" ")

        when (args[0]) {
            "participants" -> {
                if (args.size > 1) return@on
                handleParticipants()
            }
            "vote" -> {
                if (args.size != 2) return@on
                handleVote(config, args)
            }
            "votes" -> {
                if (args.size > 1) return@on
                handleVotes()
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

        channel.sendMessageEmbeds(embed).queue()
    }
}

fun GuildMessageReceivedEvent.handleVotes() {
    val votes = transaction {
        val voteResult = Votes.slice(Votes.vote, Votes.vote.count()).selectAll().groupBy(Votes.id).execute(this)
            ?: return@transaction emptyMap()
        return@transaction mutableMapOf<String, Int>().apply {
            while (voteResult.next()) {
                put(voteResult.getString(1), voteResult.getInt(2))
            }
        }
    }

    channel.sendMessageEmbeds(
        embed {
            setColor(BotColor.INFO.color)
            setTitle("Current vote status")
            setDescription(
                """
                    **Cyberpunk**: ${votes["cyberpunk"] ?: 0}
                    **Horror**: ${votes["horror"] ?: 0}
                    **Minecraft 2.0**: ${votes["mc2"] ?: 0}
                """.trimIndent()
            )
        }
    ).queue()
}

fun GuildMessageReceivedEvent.handleVote(config: Config, args: List<String>) {
    val announcements = guild.getTextChannelById(args[1]) ?: return

    message.delete().queue()

    config[Settings.STARTED] = true
    config.save()

    announcements.sendMessage(
        """
            Hey @everyone!

            We are now opening theme voting for the first official HelpChat Plugin Jam! 
            The event will begin in a week, so be sure to get your votes in before the deadline on Friday, September 3rd. 
            Be sure to sign up by typing `$PARTICIPATE_COMMAND` in ${guild.getTextChannelById(config[Settings.CHANNELS].botCommands)?.asMention} so you don't miss out!
            Please check out the ${guild.getTextChannelById(config[Settings.CHANNELS].eventInfo)?.asMention} channel for more information on the event, rules, and rewards. 
            We look forward to seeing what the community chooses!
            
            Please choose from one of the themes below to cast your vote!
            """.trimIndent()
    ).setActionRows(BUTTONS.values).queue()
    announcements
        .sendMessage("*\\*Minecraft 2.0 is basically any idea that should be in vanilla, in your opinion.*")
        .queue()
}
