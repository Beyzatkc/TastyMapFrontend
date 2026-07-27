package org.beem.tastymap.core.util

fun String.toBusinessStatusText(): String {
    return when (this.uppercase()) {
        "OPERATIONAL" -> "Açık"
        "CLOSED_TEMPORARILY" -> "Geçici Olarak Kapalı"
        "CLOSED_PERMANENTLY" -> "Kalıcı Olarak Kapalı"
        else -> if (this.isNotBlank()) this else "Bilinmiyor"
    }
}