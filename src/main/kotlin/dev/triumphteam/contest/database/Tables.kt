package dev.triumphteam.contest.database

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable

object Votes : IntIdTable() {
    val vote = varchar("vote", 15)
    val voter = long("voter")
}