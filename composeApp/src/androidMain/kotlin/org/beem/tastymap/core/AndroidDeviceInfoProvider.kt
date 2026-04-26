package org.beem.tastymap.core

import android.content.Context
import android.os.Build
import org.beem.tastymap.core.provider.DeviceInfoProvider

class AndroidDeviceInfoProvider(private val context: Context): DeviceInfoProvider {
    override fun getDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
    }

    override fun getUserAgent(): String {
        val defaultUA = android.webkit.WebSettings.getDefaultUserAgent(context)

        val marka = Build.MANUFACTURER
        val model = Build.MODEL
        val customUA = "$defaultUA [Cihaz: $marka - $model]"
        return customUA;
    }

    override suspend fun getFcmToken(): String {
        //return com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
    }
}