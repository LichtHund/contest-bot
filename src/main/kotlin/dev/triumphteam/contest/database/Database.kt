package dev.triumphteam.contest.database

import com.zaxxer.hikari.HikariDataSource
import dev.triumphteam.bukkit.feature.ApplicationFeature
import dev.triumphteam.bukkit.feature.attribute.key
import dev.triumphteam.jda.JdaApplication
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class Database {
    companion object Feature : ApplicationFeature<JdaApplication, Database, Database> {

        override val key = key<Database>("database")

        override fun install(application: JdaApplication, configure: Database.() -> Unit): Database {
            val databaseFile = File(application.applicationFolder, "contest.db").apply {
                if (!exists()) createNewFile()
            }

            val hikari = HikariDataSource().apply {
                jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"
            }

            org.jetbrains.exposed.sql.Database.connect(hikari)

            transaction {
                SchemaUtils.create(
                    Votes,
                    Participants,
                    Invites,
                )
            }

            return Database()
        }
    }
}