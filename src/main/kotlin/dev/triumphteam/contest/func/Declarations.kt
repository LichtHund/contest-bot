package dev.triumphteam.contest.func

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