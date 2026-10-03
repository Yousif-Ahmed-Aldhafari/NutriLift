package com.example.nutrilift.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val displayFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    fun today(): String = LocalDate.now().format(dateFormatter)

    fun currentTime(): String = LocalTime.now()
        .truncatedTo(ChronoUnit.MINUTES)
        .format(timeFormatter)

    fun weekStart(date: LocalDate = LocalDate.now()): String {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).format(dateFormatter)
    }

    fun monthStart(date: LocalDate = LocalDate.now()): String {
        return date.withDayOfMonth(1).format(dateFormatter)
    }

    fun thirtyDaysAgo(date: LocalDate = LocalDate.now()): String {
        return date.minusDays(29).format(dateFormatter)
    }

    fun displayDate(isoDate: String): String {
        return runCatching {
            LocalDate.parse(isoDate, dateFormatter).format(displayFormatter)
        }.getOrDefault(isoDate)
    }

    fun isValidDate(value: String): Boolean {
        return try {
            LocalDate.parse(value, dateFormatter)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    fun isValidTime(value: String): Boolean {
        return try {
            LocalTime.parse(value, timeFormatter)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }
}
