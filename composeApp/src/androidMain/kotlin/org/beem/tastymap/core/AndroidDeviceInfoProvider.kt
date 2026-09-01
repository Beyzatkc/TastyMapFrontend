package org.beem.tastymap.core

import android.content.Context
import android.os.Build
import org.beem.tastymap.core.provider.DeviceInfoProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging

class AndroidDeviceInfoProvider(private val context: Context) : DeviceInfoProvider {

    override suspend fun getDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }

    override fun getUserAgent(): String {

        val osVersion = Build.VERSION.RELEASE
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER
        val appVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "1.0.0"
        }

        return "TastyMap/$appVersion (Android $osVersion; $manufacturer $model)"
    }

    override suspend fun getFcmToken(): String {
        return try {
            Firebase.messaging.getToken()
        } catch (e: Exception) {
            println("FcmToken Error: ${e.message}")
            ""
        }
    }
}