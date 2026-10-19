package org.beem.tastymap.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
actual fun formatToRelativeDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val dateTime = LocalDateTime.parse(dateString)
        val date = dateTime.toLocalDate()
        val today = LocalDate.now()

        val daysBetween = ChronoUnit.DAYS.between(date, today).toInt()

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val timeStr = dateTime.format(timeFormatter)

        val isTurkish = Locale.getDefault().language == "tr"

        when {
            daysBetween == 0 -> if (isTurkish) "Bugün, $timeStr" else "Today, $timeStr"
            daysBetween == 1 -> if (isTurkish) "Dün, $timeStr" else "Yesterday, $timeStr"
            daysBetween in 2..6 -> if (isTurkish) "$daysBetween gün önce, $timeStr" else "$daysBetween days ago, $timeStr"
            else -> {
                val fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm", Locale.getDefault())
                dateTime.format(fullFormatter)
            }
        }
    } catch (e: Exception) {
        dateString ?: ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
actual fun formatToShortRelativeTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val dateTime = LocalDateTime.parse(dateString)
        val now = LocalDateTime.now()

        val diffInSeconds = max(1L, ChronoUnit.SECONDS.between(dateTime, now))

        val isTurkish = Locale.getDefault().language == "tr"

        val sec = "s"
        val min = if (isTurkish) "d" else "m"
        val hour = if (isTurkish) "sa" else "h"
        val day = if (isTurkish) "g" else "d"
        val week = if (isTurkish) "h" else "w"
        val year = "y" // Hem İngilizce (year) hem Türkçe (yıl) için "y" kullanabiliriz.

        val secondsInMinute = 60L
        val secondsInHour = 3600L
        val secondsInDay = 86400L
        val secondsInWeek = 604800L
        val secondsInYear = 31536000L // 365 gün

        when {
            diffInSeconds < secondsInMinute -> "$diffInSeconds$sec"
            diffInSeconds < secondsInHour -> "${diffInSeconds / secondsInMinute}$min"
            diffInSeconds < secondsInDay -> "${diffInSeconds / secondsInHour}$hour"
            diffInSeconds < secondsInYear -> {
                val diffInWeeks = diffInSeconds / secondsInWeek
                if (diffInWeeks > 0) "$diffInWeeks$week" else "${diffInSeconds / secondsInDay}$day"
            }
            else -> "${diffInSeconds / secondsInYear}$year"
        }
    } catch (e: Exception) {
        ""
    }
}