package dev.triumphteam.contest.func

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom
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

suspend inline fun <reified T> HttpClient.getOrNull(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): T? {
    return try {
        get {
            url.takeFrom(urlString)
            block()
        }
    } catch (exception: ClientRequestException) {
        null
    }
}