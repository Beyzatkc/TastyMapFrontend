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

        // TR: s (saniye), d (dakika), sa (saat), g (gün), h (hafta)
        // EN: s (second), m (minute), h (hour), d (day), w (week)
        val sec = "s"
        val min = if (isTurkish) "d" else "m"
        val hour = if (isTurkish) "sa" else "h"
        val day = if (isTurkish) "g" else "d"
        val week = if (isTurkish) "h" else "w"

        when {
            diffInSeconds < 60 -> "$diffInSeconds$sec"
            diffInSeconds < 3600 -> "${diffInSeconds / 60}$min"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600}$hour"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400}$day"
            else -> "${diffInSeconds / 604800}$week"
        }
    } catch (e: Exception) {
        ""
    }
}