package org.beem.tastymap.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toFormatTimestamp(): String {
    if (this <= 0L) return ""

    val instant = if (this < 100_000_000_000L) {
        Instant.fromEpochSeconds(this)
    } else {
        Instant.fromEpochMilliseconds(this)
    }

    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val year = localDateTime.year

    return "$day.$month.$year"
}