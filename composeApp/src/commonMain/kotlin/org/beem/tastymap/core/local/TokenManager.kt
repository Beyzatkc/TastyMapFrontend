package org.beem.tastymap.core.local
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.russhwolf.settings.get

interface TokenManager {
    fun saveTokens(accessToken: String?, refreshToken: String?);
    fun getAccessToken():String?
    fun getRefreshToken(): String?
    fun saveDeviceId(deviceId: String)
    fun getDeviceId(): String?
    fun clear()
}


class TokenManagerImpl(private val settings: Settings) : TokenManager {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_DEVICE_ID = "device_id"
    }

    override fun saveTokens(accessToken: String?, refreshToken: String?) {
        settings[KEY_ACCESS_TOKEN] = accessToken
        settings[KEY_REFRESH_TOKEN] = refreshToken
    }
    override fun saveDeviceId(deviceId: String) {
        settings[KEY_DEVICE_ID] = deviceId
    }

    override fun getAccessToken(): String? = settings[KEY_ACCESS_TOKEN]
    override fun getRefreshToken(): String? = settings[KEY_REFRESH_TOKEN]
    override fun getDeviceId(): String? = settings[KEY_DEVICE_ID]


    override fun clear() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

}