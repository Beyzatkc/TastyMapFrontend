package org.beem.tastymap.core.provider

interface DeviceInfoProvider {
    fun getDeviceId(): String
    suspend fun getFcmToken(): String
}