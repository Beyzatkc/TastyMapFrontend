package org.beem.tastymap.core.util

import kotlin.js.JsNumber
import kotlin.js.JsString

// JavaScript native Date API interop tanımları
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

actual fun formatToRelativeDateTime(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    return try {
        val targetTs = jsParseDate(dateString)
        // NaN kontrolü (JavaScript'te NaN kendine eşit değildir)
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

        when {
            daysBetween == 0 -> "Bugün, $timeStr"
            daysBetween == 1 -> "Dün, $timeStr"
            daysBetween in 2..6 -> "$daysBetween gün önce, $timeStr"
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