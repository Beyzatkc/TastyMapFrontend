package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.device.FcmTokenUpdateRequest
import org.beem.tastymap.data.model.device.NotificationPreferenceRequest
import org.beem.tastymap.data.remote.UserDeviceDataSource

class UserDeviceRepository(
    private val userDeviceDataSource: UserDeviceDataSource,
    private val deviceIdProvider: DeviceInfoProvider,
    private val settingsManager: SettingsManager
) {

    suspend fun updateFcmToken(fcmToken: String): ResultWrapper<Unit> {
        return safeApiCall {
            val deviceId = deviceIdProvider.getDeviceId()
            val request = FcmTokenUpdateRequest(deviceId,fcmToken)
            userDeviceDataSource.updateFcm(request)
        }
    }
    suspend fun updateNotificationPreference(isEnabled: Boolean): ResultWrapper<Unit> {
        val previousValue = settingsManager.isNotificationsEnabled.value
        settingsManager.setNotificationsEnabled(isEnabled)

        val result = safeApiCall {
            val deviceId = deviceIdProvider.getDeviceId()
            val request = NotificationPreferenceRequest(deviceId, isEnabled)
            userDeviceDataSource.updateNotificationPreference(request)
        }

        if (result is ResultWrapper.Error && previousValue != null) {
            settingsManager.setNotificationsEnabled(previousValue)
        }
        return result
    }
    fun observeNotificationPreference(): Flow<Boolean?> =
        settingsManager.isNotificationsEnabled.onStart {
            if (settingsManager.isNotificationsEnabled.value == null) {
                fetchAndCacheNotificationPreference()
            }
        }

    private suspend fun fetchAndCacheNotificationPreference() {
        val result = safeApiCall {
            val deviceId = deviceIdProvider.getDeviceId()
            userDeviceDataSource.getNotificationPreference(deviceId)
        }
        if (result is ResultWrapper.Success) {
            settingsManager.setNotificationsEnabled(result.data)
        }
    }
}