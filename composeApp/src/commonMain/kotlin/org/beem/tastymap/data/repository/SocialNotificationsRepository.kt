package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.NotificationsMemoryCache
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse
import org.beem.tastymap.data.remote.SocialNotificationDataSource

class SocialNotificationsRepository(
    private val dataSource: SocialNotificationDataSource,
    private val memoryCache: NotificationsMemoryCache
) {
    suspend fun getNotifications(
        page: Int = 0,
        size: Int = 10,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<SocialNotificationsResponse>> {
        if (!forceFetch) {
            val cached = memoryCache.get(page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
        }

        val result = safeApiCall {
            dataSource.getNotifications(page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put( page,result.data)
        }
        return result
    }
    suspend fun markAsRead(
    ): ResultWrapper<Unit>{
        return safeApiCall {
            dataSource.markAsRead()
        }
    }
}