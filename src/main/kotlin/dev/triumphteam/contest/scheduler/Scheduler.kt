package dev.triumphteam.contest.scheduler

import dev.triumphteam.bukkit.dsl.TriumphDsl
import dev.triumphteam.bukkit.feature.ApplicationFeature
import dev.triumphteam.bukkit.feature.attribute.key
import dev.triumphteam.bukkit.feature.featureOrNull
import dev.triumphteam.bukkit.feature.install
import dev.triumphteam.jda.JdaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Timer
import java.util.TimerTask

class Scheduler {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val timer = Timer()

    /**
     * Schedules a single run task at a specific date
     */
    fun scheduleTask(delay: Long, task: () -> Unit) {
        //scope.launch {
        timer.schedule(KippTask(task), delay)
        //}
    }

    /**
     * Schedules a repeating task at a specific date that will run every "period" in a time unit
     */
    fun scheduleRepeatingTask(period: Long, delay: Long, task: () -> Unit) {
        //scope.launch {
        timer.schedule(KippTask(task), delay, period)
        //}
    }

    companion object Feature : ApplicationFeature<JdaApplication, Scheduler, Scheduler> {

        override val key = key<Scheduler>("scheduler")

        override fun install(application: JdaApplication, configure: Scheduler.() -> Unit): Scheduler {
            return Scheduler()
        }
    }
}

class KippTask(private val task: () -> Unit) : TimerTask() {

    override fun run() = task()
}

@TriumphDsl
fun JdaApplication.repeatingTask(
    period: Duration,
    delay: Duration = period,
    task: () -> Unit
): Scheduler {
    val scheduler = featureOrNull(Scheduler) ?: install(Scheduler)
    scheduler.scheduleRepeatingTask(period.duration, delay.duration, task)
    return scheduler
}

@TriumphDsl
fun JdaApplication.runTaskLater(delay: Duration, task: () -> Unit) {
    val scheduler = featureOrNull(Scheduler) ?: install(Scheduler)
    scheduler.scheduleTask(delay.duration, task = task)
}