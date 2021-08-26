package dev.triumphteam.contest.event

import net.dv8tion.jda.api.events.GenericEvent

class EventExecutor<E : GenericEvent>(val eventClass: Class<E>, private inline val listener: E.() -> Unit) {

    fun execute(event: E) {
        listener(event)
    }

}