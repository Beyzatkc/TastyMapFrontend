package org.beem.tastymap.core.provider

interface DeviceInfoProvider {
    suspend fun getDeviceId(): String
    fun getUserAgent(): String
    suspend fun getFcmToken(): String

}