package org.beem.tastymap.core

import org.beem.tastymap.core.provider.DeviceInfoProvider
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging



class IosDeviceInfoProvider: DeviceInfoProvider {
    override fun getDeviceId(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_ios_id"
    }

    override fun getUserAgent(): String {
        val device = UIDevice.currentDevice
        val appName = NSBundle.mainBundle.infoDictionary?.get("CFBundleName") ?: "TastyMap"
        val appVersion = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") ?: "1.0"

        val model = device.model
        val sysName = device.systemName
        val sysVersion = device.systemVersion

        return "$appName/$appVersion ($model; $sysName $sysVersion; ${device.name})"
    }

    override suspend fun getFcmToken(): String {
        return Firebase.messaging.getToken() ?: ""
    }

}