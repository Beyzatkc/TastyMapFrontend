package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.support.CreateSupportTicketRequest
import org.beem.tastymap.data.model.support.SupportCategory
import org.beem.tastymap.data.remote.SupportDataSource

class SupportRepository(
    private val remoteDataSource: SupportDataSource,
    private val deviceInfoProvider: DeviceInfoProvider
) {
    suspend fun addSupport(
        category: SupportCategory,
        message: String,
        appVersion: String
    ): ResultWrapper<Unit> {
        val request = CreateSupportTicketRequest(
            category = category,
            message = message.trim(),
            appVersion = appVersion,
            deviceInfo = deviceInfoProvider.getDeviceInfo()
        )
        return safeApiCall {
            remoteDataSource.addSupport(request)
        }
    }
}