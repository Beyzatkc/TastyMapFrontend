package org.beem.tastymap.core.provider

interface DeviceInfoProvider {
    fun getDeviceId(): String
    fun getUserAgent(): String

}