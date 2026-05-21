package org.beem.tastymap.core
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.BuildKonfig
import org.beem.tastymap.core.util.await
import kotlin.js.JsString
import kotlin.js.Promise

external interface Crypto {
    fun randomUUID(): String
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
val crypto = js("window.crypto") as Crypto


external fun getFcmTokenJs(vapidKey: String): Promise<JsString>
class WebDeviceInfoProvider: DeviceInfoProvider {
    override fun getDeviceId(): String {
        val key = "browser_device_id"
        val existingId = localStorage.getItem(key)

        return if (existingId != null && existingId.isNotBlank()) {
            existingId
        } else {
            val newId = "web-" + crypto.randomUUID()
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
