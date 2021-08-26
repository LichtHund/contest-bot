package dev.triumphteam.contest.config

import dev.triumphteam.bukkit.configuration.BaseConfig
import dev.triumphteam.bukkit.feature.ApplicationFeature
import dev.triumphteam.bukkit.feature.attribute.AttributeKey
import dev.triumphteam.bukkit.feature.attribute.key
import dev.triumphteam.jda.JdaApplication
import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Name
import me.mattstudios.config.properties.Property
import java.io.File
import java.nio.file.Path

class Config(dataFolder: File) : BaseConfig(Path.of(dataFolder.absolutePath, "config.yml"), Settings::class.java) {

    companion object Feature : ApplicationFeature<JdaApplication, Config, Config> {

        override val key = key<Config>("config")

        override fun install(application: JdaApplication, configure: Config.() -> Unit): Config {
            return Config(application.applicationFolder)
        }
    }
}

object Settings : SettingsHolder {

    @me.mattstudios.config.annotations.Path("channels")
    val CHANNELS = Property.create(Channels())

}

data class Channels(
    @Name("bot-commands")
    var botCommands: String = "",
    @Name("event-info")
    var eventInfo: String = "",
    @Name("contest-log")
    var contestLog: String = "",
)