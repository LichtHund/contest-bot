package dev.triumphteam.contest.func

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

fun tokenFromFlag(args: Array<String>): String {
    val cli = DefaultParser().parse(
        Options().apply {
            addOption(Option.builder("t").hasArg().argName("token").required().build())
        },
        args
    )

    return cli.getOptionValue("t")
}

fun embed(builder: EmbedBuilder.() -> Unit): MessageEmbed {
    return EmbedBuilder().apply(builder).build()
}

fun String.plural(list: List<*>) = if (list.size != 1) plus('s') else this