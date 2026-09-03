package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.SubscribeMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import org.beem.tastymap.data.model.subscribers.SubscribeStatus
import org.beem.tastymap.data.remote.SubscribersDataSource


class SubscribersRepository(
    private val dataSource: SubscribersDataSource,
    private val localDataSource: ProfileLocalDataSource,
    private val memoryCache: SubscribeMemoryCache
) {

    suspend fun subscribe(targetUserId: Long, myUserId: Long): ResultWrapper<SubscribeResponse> {
        val result = safeApiCall { dataSource.subscribe(targetUserId) }

        if (result is ResultWrapper.Success) {
            val status = result.data.relationStatus

            if (status == SubscribeStatus.ACCEPTED) {
                localDataSource.incrementSubscribed(myUserId)
                localDataSource.incrementSubscriber(targetUserId)
            }

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }

        return result
    }

    suspend fun acceptSubscribeRequest(requesterId: Long, myUserId: Long): ResultWrapper<Unit> {
        val result = safeApiCall { dataSource.acceptSubscribeRequest(requesterId) }
        if (result is ResultWrapper.Success) {
            // Benim takipçi sayım (subscriberCount) +1
            localDataSource.incrementSubscriber(myUserId)
            // İsteği atan kişinin takip ettiği sayısı (subscribedCount) +1
            localDataSource.incrementSubscribed(requesterId)

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(requesterId)
        }
        return result
    }

    suspend fun rejectSubscribeRequest(requesterId: Long): ResultWrapper<Unit> {
        return safeApiCall {
            dataSource.rejectSubscribeRequest(requesterId)
        }
    }

    suspend fun unSubscribe(targetUserId: Long, myUserId: Long): ResultWrapper<Unit> {
        val result = safeApiCall { dataSource.unSubscribe(targetUserId) }
        if (result is ResultWrapper.Success) {
            // Benim takip ettiğim sayısı -1
            localDataSource.decrementSubscribed(myUserId)
            // Karşı tarafın takipçisi -1
            localDataSource.decrementSubscriber(targetUserId)

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }
        return result
    }

    suspend fun unSubscriber(targetUserId: Long, myUserId: Long): ResultWrapper<Unit> {
        val result = safeApiCall { dataSource.unSubscriber(targetUserId) }
        if (result is ResultWrapper.Success) {
            // Benim takipçi sayım -1
            localDataSource.decrementSubscriber(myUserId)
            // Çıkardığım kişinin takip ettiği sayısı -1
            localDataSource.decrementSubscribed(targetUserId)

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }
        return result
    }

    suspend fun getUserSubscribes(
        userId: Long,
        page: Int = 0,
        size: Int = 10
    ): ResultWrapper<PageResponse<SubscribeResponse>> {
        val cached = memoryCache.get(userId, TYPE_SUBSCRIBES, page)
        if (cached != null) {
            return ResultWrapper.Success(cached)
        }

        val result = safeApiCall {
            dataSource.getUserSubscribes(userId, page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(userId, TYPE_SUBSCRIBES, page, result.data)
        }

        return result
    }
    suspend fun getUserSubscribers(
        userId: Long,
        page: Int = 0,
        size: Int = 10
    ): ResultWrapper<PageResponse<SubscribeResponse>> {
        val cached = memoryCache.get(userId, TYPE_SUBSCRIBERS, page)
        if (cached != null) {
            return ResultWrapper.Success(cached)
        }

        val result = safeApiCall {
            dataSource.getUserSubscribers(userId, page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(userId, TYPE_SUBSCRIBERS, page, result.data)
        }

        return result
    }

    suspend fun getPendingRequests(
        page: Int = 0,
        size: Int = 10
    ): ResultWrapper<PageResponse<SubscribeResponse>> {
        return safeApiCall {
            dataSource.getPendingRequests(page, size)
        }
    }

    companion object {
        private const val TYPE_SUBSCRIBES = "SUBSCRIBES"
        private const val TYPE_SUBSCRIBERS = "SUBSCRIBERS"
    }

}






















