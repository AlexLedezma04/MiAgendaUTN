package org.agenda.utn.util

import kotlinx.datetime.*

object DateUtils {

    fun formatDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        return "${localDate.dayOfMonth.toString().padStart(2, '0')}/" +
                "${localDate.monthNumber.toString().padStart(2, '0')}/" +
                "${localDate.year}"
    }

    fun formatDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return "${localDateTime.dayOfMonth.toString().padStart(2, '0')}/" +
                "${localDateTime.monthNumber.toString().padStart(2, '0')}/" +
                "${localDateTime.year} " +
                "${localDateTime.hour.toString().padStart(2, '0')}:" +
                "${localDateTime.minute.toString().padStart(2, '0')}"
    }

    fun getTodayStartTimestamp(): Long {
        val now = Clock.System.now()
        val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        return startOfDay.toEpochMilliseconds()
    }

    fun getTimestampFromDate(year: Int, month: Int, day: Int): Long {
        val localDate = LocalDate(year, month, day)
        val instant = localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
        return instant.toEpochMilliseconds()
    }

    fun isPastDate(timestamp: Long): Boolean {
        return timestamp < Clock.System.now().toEpochMilliseconds()
    }

    fun isToday(timestamp: Long): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val taskDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        return taskDate == today
    }

    fun getRelativeDateDescription(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val taskDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        return when {
            taskDate == today -> "Hoy"
            taskDate == today.plus(1, DateTimeUnit.DAY) -> "Mañana"
            taskDate == today.minus(1, DateTimeUnit.DAY) -> "Ayer"
            taskDate < today -> "Vencida"
            else -> formatDate(timestamp)
        }
    }
}