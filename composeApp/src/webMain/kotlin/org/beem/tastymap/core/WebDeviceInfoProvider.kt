package org.beem.tastymap.core
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.core.util.BuildKonfig
import org.beem.tastymap.core.util.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.js.JsString
import kotlin.js.Promise


external class TextEncoder {
    fun encode(input: String): Uint8Array
}
external interface SubtleCrypto {
    fun digest(algorithm: String, data: Uint8Array): Promise<ArrayBuffer>
}
external interface Crypto {
    val subtle: SubtleCrypto
    fun randomUUID(): String
}


private fun getCrypto(): Crypto = js("window.crypto")



external fun getFcmTokenJs(vapidKey: String): Promise<JsString>
class WebDeviceInfoProvider: DeviceInfoProvider {


    override suspend fun getDeviceId(): String {
        val raw = buildString {
            append(window.navigator.userAgent)
            append("|")
            append(window.navigator.language)
            append("|")
            append("${window.screen.width}x${window.screen.height}")
            append("|")
            append(window.navigator.platform)
        }

        return sha256(raw)
    }

    override fun getUserAgent(): String {
        return window.navigator.userAgent
    }

    override suspend fun getFcmToken(): String {
        val jsPromise = getFcmTokenJs(BuildKonfig.VAPID_KEY)
        println("VAPID = ${BuildKonfig.VAPID_KEY}")
        val token = jsPromise.await()
        return token.toString()
    }

    suspend fun sha256(input: String): String {
        val encoder = TextEncoder()
        val data = encoder.encode(input)

        val buffer = getCrypto().subtle.digest("SHA-256", data).await()

        val hashArray = Uint8Array(buffer)
        return buildString {
            for (i in 0 until hashArray.length) {
                val hex = (hashArray.get(i).toInt() and 0xFF).toString(16)
                if (hex.length == 1) append("0")
                append(hex)
            }
        }
    }
}
