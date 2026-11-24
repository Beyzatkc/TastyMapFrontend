package org.beem.tastymap.core.util.iso

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val SECONDS_PER_DAY = 86_400L
private const val TURKEY_OFFSET_SECONDS = 3 * 3600L // UTC+3 (Türkiye saati farkı)

/**
 * ISO-8601 tarih string'ini ("2026-11-12T17:47:59.16744") parse ederek
 * ilgili zaman grubuna atar.
 */
@OptIn(ExperimentalTime::class)
fun resolveTimeGroup(isoDateString: String): TimeGroup {
    return try {
        val datePart = isoDateString.substringBefore("T") // "2026-11-12"
        val sessionDate = LocalDate.parse(datePart)

        // expect/actual veya Instant kullanmadan doğrudan bugünün tarihini bul:
        val nowEpochSeconds = Clock.System.now().epochSeconds
        val todayEpochDays = ((nowEpochSeconds + TURKEY_OFFSET_SECONDS) / SECONDS_PER_DAY).toInt()
        val today = LocalDate.fromEpochDays(todayEpochDays)

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