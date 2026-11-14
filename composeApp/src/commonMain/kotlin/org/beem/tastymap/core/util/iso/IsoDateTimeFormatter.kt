package org.beem.tastymap.core.util.iso

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.beem.tastymap.core.util.currentEpochMillis

/**
 * ISO-8601 tarih string'ini ("2026-11-12T17:47:59.16744") parse ederek
 * ilgili zaman grubuna atar.
 */
fun resolveTimeGroup(isoDateString: String): TimeGroup {
    return try {
        val datePart = isoDateString.substringBefore("T") // "2026-11-12"
        val sessionDate = LocalDate.parse(datePart)

        val today = Instant.fromEpochMilliseconds(currentEpochMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        when {
            sessionDate == today -> TimeGroup.TODAY
            sessionDate == today.minus(1, DateTimeUnit.DAY) -> TimeGroup.YESTERDAY
            sessionDate >= today.minus(7, DateTimeUnit.DAY) -> TimeGroup.LAST_7_DAYS
            sessionDate >= today.minus(30, DateTimeUnit.DAY) -> TimeGroup.LAST_30_DAYS
            else -> TimeGroup.OLDER
        }
    } catch (e: Exception) {
        TimeGroup.OLDER
    }
}