package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.FcmTokenUpdateRequest
import org.beem.tastymap.data.remote.UserDeviceDataSource

class UserDeviceRepository(
    private val userDeviceDataSource: UserDeviceDataSource,
    private val deviceIdProvider: DeviceInfoProvider
) {

    suspend fun updateFcmToken(fcmToken: String): ResultWrapper<Unit> {
        return safeApiCall {
            val deviceId = deviceIdProvider.getDeviceId()
            val request = FcmTokenUpdateRequest(deviceId,fcmToken)
            userDeviceDataSource.updateFcm(request)
        }
    }
}