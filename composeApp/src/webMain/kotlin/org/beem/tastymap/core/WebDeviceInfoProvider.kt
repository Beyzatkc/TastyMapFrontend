package org.beem.tastymap.core
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.BuildKonfig
import org.beem.tastymap.core.util.await
import kotlin.js.JsString
import kotlin.js.Promise



private external val window: Window
private external val localStorage: Storage

private external interface Window {
    val navigator: Navigator
}

private external interface Navigator {
    val userAgent: String
}
external fun getFcmTokenJs(vapidKey: String): Promise<JsString>

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

    override suspend fun getFcmToken(): String {
        val jsPromise = getFcmTokenJs(BuildKonfig.VAPID_KEY)
        val token = jsPromise.await()
        return token.toString()
    }


}
