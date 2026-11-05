package org.beem.tastymap.core.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class PlaceOpenStatus {
    OPEN,
    CLOSED,
    UNKNOWN
}

data class ParsedDayHours(
    val dayName: String,
    val rawHours: String,
    val isToday: Boolean,
    val is24Hours: Boolean = false,
    val isClosedAllDay: Boolean = false
)

/**
 * Kotlinx DateTime DayOfWeek -> Türkçe Gün Adı Extension
 */
fun DayOfWeek.toTurkishName(): String {
    return when (this) {
        DayOfWeek.MONDAY -> "Pazartesi"
        DayOfWeek.TUESDAY -> "Salı"
        DayOfWeek.WEDNESDAY -> "Çarşamba"
        DayOfWeek.THURSDAY -> "Perşembe"
        DayOfWeek.FRIDAY -> "Cuma"
        DayOfWeek.SATURDAY -> "Cumartesi"
        DayOfWeek.SUNDAY -> "Pazar"
        else -> ""
    }
}

/**
 * Cihazın bugünkü gün adını döner.
 */
fun getCurrentTurkishDay(): String {
    return try {
        // kotlin.time.Clock.System.now() -> epoch ms -> kotlinx Instant -> LocalDateTime
        val epochMillis = Clock.System.now().toEpochMilliseconds()
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        now.dayOfWeek.toTurkishName()
    } catch (_: Exception) {
        ""
    }
}

/**
 * "Pazartesi: 09:00–21:00" formatındaki listeyi ayrıştırır ve bugünün çalışma durumunu hesaplar.
 */
fun List<String>.calculateTodayStatus(): Pair<PlaceOpenStatus, String?> {
    if (this.isEmpty()) return Pair(PlaceOpenStatus.UNKNOWN, null)

    val todayName = getCurrentTurkishDay()
    val todayRow = this.firstOrNull { it.startsWith(todayName, ignoreCase = true) } ?: return Pair(PlaceOpenStatus.UNKNOWN, null)

    val hoursPart = todayRow.substringAfter(":").trim()

    val lower = hoursPart.lowercase()
    if (lower.contains("24 saat açık") || lower.contains("open 24 hours")) {
        return Pair(PlaceOpenStatus.OPEN, "24 Saat Açık")
    }
    if (lower.contains("kapalı") || lower.contains("closed")) {
        return Pair(PlaceOpenStatus.CLOSED, "Tüm gün kapalı")
    }

    return try {
        val epochMillis = Clock.System.now().toEpochMilliseconds()
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinutes = now.hour * 60 + now.minute

        val cleanHours = hoursPart.replace("–", "-").replace("—", "-")
        val parts = cleanHours.split("-")

        if (parts.size == 2) {
            val openMinutes = parseTimeToMinutes(parts[0].trim())
            val closeMinutes = parseTimeToMinutes(parts[1].trim())

            val isOpen = if (closeMinutes < openMinutes) {
                currentMinutes >= openMinutes || currentMinutes < closeMinutes
            } else {
                currentMinutes in openMinutes until closeMinutes
            }

            val status = if (isOpen) PlaceOpenStatus.OPEN else PlaceOpenStatus.CLOSED
            Pair(status, hoursPart)
        } else {
            Pair(PlaceOpenStatus.UNKNOWN, hoursPart)
        }
    } catch (_: Exception) {
        Pair(PlaceOpenStatus.UNKNOWN, hoursPart)
    }
}

private fun parseTimeToMinutes(timeStr: String): Int {
    val parts = timeStr.split(":")
    val hour = parts[0].trim().toInt()
    val minute = if (parts.size > 1) parts[1].trim().toInt() else 0
    return hour * 60 + minute
}