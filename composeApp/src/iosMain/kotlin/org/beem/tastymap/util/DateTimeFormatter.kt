package org.beem.tastymap.core.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.timeIntervalSinceDate
import kotlin.math.max

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

        val isTurkish = NSLocale.currentLocale.languageCode == "tr"

        when {
            daysBetween == 0 -> if (isTurkish) "Bugün, $timeStr" else "Today, $timeStr"
            daysBetween == 1 -> if (isTurkish) "Dün, $timeStr" else "Yesterday, $timeStr"
            daysBetween in 2..6 -> if (isTurkish) "$daysBetween gün önce, $timeStr" else "$daysBetween days ago, $timeStr"
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
actual fun formatToShortRelativeTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val isoFormatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        }

        var date = isoFormatter.dateFromString(dateString)
        if (date == null) {
            isoFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
            date = isoFormatter.dateFromString(dateString)
        }

        if (date == null) return ""

        val now = NSDate()
        val timeInterval = now.timeIntervalSinceDate(date)
        val diffInSeconds = max(1L, timeInterval.toLong())

        val isTurkish = NSLocale.currentLocale.languageCode == "tr"

        val sec = "s"
        val min = if (isTurkish) "d" else "m"
        val hour = if (isTurkish) "sa" else "h"
        val day = if (isTurkish) "g" else "d"
        val week = if (isTurkish) "h" else "w"
        val year = "y"

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