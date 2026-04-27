package org.beem.tastymap.core
import org.beem.tastymap.core.provider.DeviceInfoProvider

private external val window: Window
private external val localStorage: Storage

private external interface Window {
    val navigator: Navigator
}

private external interface Navigator {
    val userAgent: String
}

private external interface Storage {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}


class WebDeviceInfoProvider: DeviceInfoProvider {
    override fun getDeviceId(): String {
        val key = "browser_device_id"
        val existingId = localStorage.getItem(key)

        return if (existingId != null && existingId.isNotBlank()) {
            existingId
        } else {
            val newId = "web-${(1..12).map { ('a'..'z').random() }.joinToString("")}"
            localStorage.setItem(key, newId)
            newId
        }
    }

    override fun getUserAgent(): String {
        return window.navigator.userAgent
    }


}