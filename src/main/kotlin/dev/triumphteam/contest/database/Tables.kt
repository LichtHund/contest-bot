package dev.triumphteam.contest.database

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable

object Votes : IntIdTable() {
    val vote = varchar("vote", 15)
    val voter = long("voter")
}

object Participants : IntIdTable() {
    val repo = varchar("repo", 512)
    val leader = long("leader")
    val partner = long("partner").nullable()
}

object Invites : IntIdTable() {
    val team = reference("team", Participants.id)
    val partner = long("partner")
}