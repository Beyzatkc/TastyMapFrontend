package org.beem.tastymap.core.util

fun String?.parseDeviceName(): String {
    if (this.isNullOrBlank()) return "Bilinmeyen Cihaz"

    val customDeviceRegex = Regex("\\[Cihaz:\\s*([^\\]]+)\\]")
    val customMatch = customDeviceRegex.find(this)
    if (customMatch != null) {
        return customMatch.groupValues[1].trim()
    }

    val browser = when {
        this.contains("Edg", ignoreCase = true) -> "Edge"
        this.contains("Chrome", ignoreCase = true) -> "Chrome"
        this.contains("Firefox", ignoreCase = true) -> "Firefox"
        this.contains("Safari", ignoreCase = true) && !this.contains("Chrome", ignoreCase = true) -> "Safari"
        else -> "Tarayıcı"
    }
    val os = when {
        this.contains("Windows NT 10.0", ignoreCase = true) -> "Windows 10/11"
        this.contains("Windows NT 6.3", ignoreCase = true) -> "Windows 8.1"
        this.contains("Windows NT 6.1", ignoreCase = true) -> "Windows 7"
        this.contains("Windows", ignoreCase = true) -> "Windows"
        this.contains("Macintosh", ignoreCase = true) || this.contains("Mac OS", ignoreCase = true) -> "macOS"
        this.contains("Android", ignoreCase = true) -> "Android"
        this.contains("iPhone", ignoreCase = true) || this.contains("iPad", ignoreCase = true) -> "iOS"
        this.contains("Linux", ignoreCase = true) -> "Linux"
        else -> null
    }

    return if (os != null) {
        "$os - $browser"
    } else {
        browser
    }
}