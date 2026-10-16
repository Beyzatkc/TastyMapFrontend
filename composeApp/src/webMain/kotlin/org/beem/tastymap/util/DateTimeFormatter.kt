package org.beem.tastymap.core.util

import kotlin.js.JsNumber
import kotlin.js.JsString
import kotlin.math.max

// JavaScript native Date ve Browser API interop tanımları

@JsFun("(dStr) => new Date(dStr).getTime()")
private external fun jsParseDate(dateStr: String): Double

@JsFun("() => new Date().getTime()")
private external fun jsNow(): Double

@JsFun("(ts) => new Date(ts).getFullYear()")
private external fun jsGetFullYear(ts: Double): Int

@JsFun("(ts) => new Date(ts).getMonth()")
private external fun jsGetMonth(ts: Double): Int

@JsFun("(ts) => new Date(ts).getDate()")
private external fun jsGetDate(ts: Double): Int

@JsFun("(ts) => new Date(ts).getHours()")
private external fun jsGetHours(ts: Double): Int

@JsFun("(ts) => new Date(ts).getMinutes()")
private external fun jsGetMinutes(ts: Double): Int

@JsFun("(y, m, d) => new Date(y, m, d).getTime()")
private external fun jsMakeDate(y: Int, m: Int, d: Int): Double

@JsFun("() => (typeof navigator !== 'undefined' && navigator.language ? navigator.language.toLowerCase().startsWith('tr') : true)")
private external fun jsIsTurkish(): Boolean

actual fun formatToRelativeDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val targetTs = jsParseDate(dateString)
        if (targetTs != targetTs) return dateString

        val nowTs = jsNow()

        val targetYear = jsGetFullYear(targetTs)
        val targetMonth = jsGetMonth(targetTs)
        val targetDay = jsGetDate(targetTs)

        val nowYear = jsGetFullYear(nowTs)
        val nowMonth = jsGetMonth(nowTs)
        val nowDay = jsGetDate(nowTs)

        val targetDayStart = jsMakeDate(targetYear, targetMonth, targetDay)
        val todayStart = jsMakeDate(nowYear, nowMonth, nowDay)

        val diffTime = todayStart - targetDayStart
        val daysBetween = (diffTime / (1000 * 60 * 60 * 24)).toInt()

        val hours = jsGetHours(targetTs).toString().padStart(2, '0')
        val minutes = jsGetMinutes(targetTs).toString().padStart(2, '0')
        val timeStr = "$hours:$minutes"

        val isTurkish = jsIsTurkish()

        when {
            daysBetween == 0 -> if (isTurkish) "Bugün, $timeStr" else "Today, $timeStr"
            daysBetween == 1 -> if (isTurkish) "Dün, $timeStr" else "Yesterday, $timeStr"
            daysBetween in 2..6 -> if (isTurkish) "$daysBetween gün önce, $timeStr" else "$daysBetween days ago, $timeStr"
            else -> {
                val day = targetDay.toString().padStart(2, '0')
                val month = (targetMonth + 1).toString().padStart(2, '0')
                "$day.$month.$targetYear - $timeStr"
            }
        }
    } catch (e: Throwable) {
        dateString
    }
}

actual fun formatToShortRelativeTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val targetTs = jsParseDate(dateString)

        if (targetTs != targetTs) return ""

        val nowTs = jsNow()

        val diffInSeconds = max(1L, ((nowTs - targetTs) / 1000).toLong())

        val isTurkish = jsIsTurkish()

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
    } catch (e: Throwable) {
        ""
    }
}