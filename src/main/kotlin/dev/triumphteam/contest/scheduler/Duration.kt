package dev.triumphteam.contest.scheduler

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@JvmInline
value class Duration(val duration: Long)

fun days(days: Long) = Duration(TimeUnit.DAYS.toMillis(days))
fun minutes(minutes: Long) = Duration(TimeUnit.MINUTES.toMillis(minutes))
fun hours(hours: Long) = Duration(TimeUnit.HOURS.toMillis(hours))
fun seconds(seconds: Long) = Duration(TimeUnit.SECONDS.toMillis(seconds))

val MINUTES_TILL_NOON: Duration
    get() = minutes(LocalDateTime.now().until(LocalDate.now().atTime(12, 0, 30), ChronoUnit.MINUTES))

val TEST_TIME: Duration
    get() = minutes(LocalDateTime.now().until(LocalDate.now().atTime(2, 30, 30), ChronoUnit.MINUTES))