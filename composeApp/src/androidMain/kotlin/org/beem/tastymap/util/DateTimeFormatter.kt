package org.beem.tastymap.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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

        when {
            daysBetween == 0 -> "Bugün, $timeStr"
            daysBetween == 1 -> "Dün, $timeStr"
            daysBetween in 2..6 -> "$daysBetween gün önce, $timeStr"
            else -> {
                val fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm", Locale.getDefault())
                dateTime.format(fullFormatter)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}