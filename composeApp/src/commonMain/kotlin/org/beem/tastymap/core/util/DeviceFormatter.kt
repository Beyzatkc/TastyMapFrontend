package org.beem.tastymap.core.util

fun String?.parseDeviceName(): String {
    if (this.isNullOrBlank()) return "Bilinmeyen Cihaz"

    val regex = Regex("\\[Cihaz:\\s*([^\\]]+)\\]")
    val matchResult = regex.find(this)

    return if (matchResult != null) {
        matchResult.groupValues[1].trim()
    } else {
        if (this.contains("Android", ignoreCase = true)) "Android Cihaz"
        else if (this.contains("iPhone", ignoreCase = true)) "iPhone"
        else "Bilinmeyen Cihaz"
    }
}