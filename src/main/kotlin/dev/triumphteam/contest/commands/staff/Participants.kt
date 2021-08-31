package dev.triumphteam.contest.commands.staff

import dev.triumphteam.contest.database.Participants
import dev.triumphteam.contest.event.on
import dev.triumphteam.contest.func.BotColor
import dev.triumphteam.contest.func.embed
import dev.triumphteam.jda.JdaApplication
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.Component
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import java.net.URL
import kotlin.math.ceil

private val PAGE_REGEX = "vote-(\\d+)-(\\d+)-(\\w+)".toRegex()

fun GuildMessageReceivedEvent.handleParticipants() {
    val counts = transaction {
        val teams = Participants.selectAll()
        val members = teams.sumOf {
            mutableListOf<Long?>().apply {
                add(it[Participants.leader])
                add(it[Participants.partner])
            }.filterNotNull().count()
        }

        teams.count() to members
    }

    val max = ceil(counts.first / 10.0).toLong()
    val page = getParticipantPage(guild, 1, max, "There are **${counts.first}** teams and **${counts.second}** users.")

    message.replyEmbeds(page)
        .mentionRepliedUser(false)
        .setActionRow(Button.primary("vote-1-$max-next", "▶"))
        .queue()
}

fun JdaApplication.pageListener() {
    on<ButtonClickEvent> {
        val button = button ?: return@on
        val guild = guild ?: return@on
        val description = message?.getDescription() ?: return@on
        val (pageString, maxString, type) = button.id?.let { PAGE_REGEX.matchEntire(it)?.destructured } ?: return@on
        var pageNum = pageString.toLongOrNull() ?: return@on
        val maxNum = maxString.toLongOrNull() ?: return@on

        pageNum += if (type == "next") 1 else -1

        val page = getParticipantPage(guild, pageNum, maxNum, description)
        val actionRow = actionRow {
            if (pageNum > 1) {
                append(Button.primary("vote-$pageNum-$maxNum-prev", "◀"))
            }

            if (pageNum < maxNum) {
                append(Button.primary("vote-$pageNum-$maxNum-next", "▶"))
            }
        }

        editMessageEmbeds(page).setActionRows(actionRow).queue()
    }
}

fun getParticipantPage(guild: Guild, page: Long, max: Long, description: String): MessageEmbed {
    return transaction {
        val data = Participants.selectAll().limit(10, (page - 1) * 10).map {
            val members = mutableListOf<Member?>().apply {
                add(guild.getMemberById(it[Participants.leader]))
                add(it[Participants.partner]?.let { id -> guild.getMemberById(id) })
            }.filterNotNull()

            it[Participants.repo] to members
        }

        val repos = data.map { it.first }.joinToString("\n") {
            val url = try {
                URL(it).path.removePrefix("/")
            } catch (ignored: Exception) {
                "Error"
            }
            val trimmed = if (url.length >= 25) url.substring(0, 25).plus("...") else url
            "[$trimmed]($it)"
        }

        val members = data.map { it.second }.joinToString("\n") { it.joinToString(", ") { member -> member.asMention } }

        embed {
            setColor(BotColor.INFO.color)
            setTitle("Participants")
            addField("Repositories", repos, true)
            addField("Members", members, true)
            setDescription(description)
            setFooter("Page: $page/$max")
        }
    }
}

private fun Message.getDescription(): String? {
    return embeds.firstOrNull()?.description
}

private fun actionRow(builder: ActionRowBuilder.() -> Unit): ActionRow {
    return ActionRowBuilder().apply(builder).build()
}

private class ActionRowBuilder {
    val components = mutableListOf<Component>()

    fun append(component: Component) {
        components.add(component)
    }

    fun build(): ActionRow {
        return ActionRow.of(components)
    }
}