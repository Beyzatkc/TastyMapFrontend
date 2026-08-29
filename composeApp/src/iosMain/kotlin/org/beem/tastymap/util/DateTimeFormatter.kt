package org.beem.tastymap.core.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun formatToRelativeDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val isoFormatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        }

        var date = isoFormatter.dateFromString(dateString)
        if (date == null) {
            // Kesirli saniye içermeyen alternatif ISO biçimi denemesi
            isoFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
            date = isoFormatter.dateFromString(dateString)
        }

        if (date == null) return dateString

        val calendar = NSCalendar.currentCalendar
        val now = NSDate()

        val components = calendar.components(
            NSCalendarUnitDay,
            fromDate = date,
            toDate = now,
            options = 0u
        )
        val daysBetween = components.day.toInt()

        val timeFormatter = NSDateFormatter().apply {
            dateFormat = "HH:mm"
            locale = NSLocale.currentLocale
        }
        val timeStr = timeFormatter.stringFromDate(date)

        when {
            daysBetween == 0 -> "Bugün, $timeStr"
            daysBetween == 1 -> "Dün, $timeStr"
            daysBetween in 2..6 -> "$daysBetween gün önce, $timeStr"
            else -> {
                val fullFormatter = NSDateFormatter().apply {
                    dateFormat = "dd.MM.yyyy - HH:mm"
                    locale = NSLocale.currentLocale
                }
                fullFormatter.stringFromDate(date)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}