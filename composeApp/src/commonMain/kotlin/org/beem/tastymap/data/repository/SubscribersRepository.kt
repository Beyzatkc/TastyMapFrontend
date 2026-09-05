package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.SubscribeMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.subscribers.SubscribeActionResult
import org.beem.tastymap.data.model.subscribers.SubscribeResponse
import org.beem.tastymap.data.remote.SubscribersDataSource
import org.beem.tastymap.domain.model.RelationStatus

class SubscribersRepository(
    private val dataSource: SubscribersDataSource,
    private val localDataSource: ProfileLocalDataSource,
    private val memoryCache: SubscribeMemoryCache
) {

    // 1. Takip Et / İstek Gönder
    suspend fun subscribe(targetUserId: Long, myUserId: Long): ResultWrapper<SubscribeActionResult> {
        val result = safeApiCall { dataSource.subscribe(targetUserId) }

        if (result is ResultWrapper.Success) {
            val actionResult = result.data

            // Eğer doğrudan ACCEPTED (Açık profil) olduysa sayaçlar 1 artar
            if (actionResult.relationStatus == RelationStatus.FOLLOWING) {
                localDataSource.incrementSubscribed(myUserId)
                localDataSource.incrementSubscriber(targetUserId)
            }

            localDataSource.updateRelationStatus(
                userId = targetUserId,
                relationStatus = actionResult.relationStatus,
                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest
            )

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }

        return result
    }

    // 2. Gelen İsteği Onayla
    suspend fun acceptSubscribeRequest(requesterId: Long, myUserId: Long): ResultWrapper<SubscribeActionResult> {
        val result = safeApiCall { dataSource.acceptSubscribeRequest(requesterId) }

        if (result is ResultWrapper.Success) {
            val actionResult = result.data

            // Benim takipçim +1, karşı tarafın takip ettiği +1
            localDataSource.incrementSubscriber(myUserId)
            localDataSource.incrementSubscribed(requesterId)

            localDataSource.updateRelationStatus(
                userId = requesterId,
                relationStatus = actionResult.relationStatus,
                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest
            )

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(requesterId)
        }

        return result
    }

    // 3. Gelen İsteği Reddet
    suspend fun rejectSubscribeRequest(requesterId: Long, myUserId: Long): ResultWrapper<SubscribeActionResult> {
        val result = safeApiCall { dataSource.rejectSubscribeRequest(requesterId) }

        if (result is ResultWrapper.Success) {
            val actionResult = result.data

            // Reddedilince sayaç değişmez, sadece gelen istek bayrağı sıfırlanır
            localDataSource.updateRelationStatus(
                userId = requesterId,
                relationStatus = actionResult.relationStatus,
                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest
            )

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(requesterId)
        }

        return result
    }

    // 4. Takipten Çık / Gönderilen İsteği İptal Et
    suspend fun unSubscribe(targetUserId: Long, myUserId: Long): ResultWrapper<SubscribeActionResult> {
        val result = safeApiCall { dataSource.unSubscribe(targetUserId) }

        if (result is ResultWrapper.Success) {
            val actionResult = result.data

            // Eğer önceden PENDING değil de takip ediyorsak sayaçları azaltırız
            // unSubscribe sonrası relationStatus artık FOLLOWING değilse sayaç düşülür.
            localDataSource.decrementSubscribed(myUserId)
            localDataSource.decrementSubscriber(targetUserId)

            localDataSource.updateRelationStatus(
                userId = targetUserId,
                relationStatus = actionResult.relationStatus,
                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest
            )

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }

        return result
    }

    // 5. Takipçiyi Çıkar
    suspend fun unSubscriber(targetUserId: Long, myUserId: Long): ResultWrapper<SubscribeActionResult> {
        val result = safeApiCall { dataSource.unSubscriber(targetUserId) }

        if (result is ResultWrapper.Success) {
            val actionResult = result.data

            // Benim takipçim -1, karşı tarafın takip ettiği -1
            localDataSource.decrementSubscriber(myUserId)
            localDataSource.decrementSubscribed(targetUserId)

            localDataSource.updateRelationStatus(
                userId = targetUserId,
                relationStatus = actionResult.relationStatus,
                hasPendingIncomingRequest = actionResult.hasPendingIncomingRequest
            )

            memoryCache.invalidateUserCache(myUserId)
            memoryCache.invalidateUserCache(targetUserId)
        }

        return result
    }

    suspend fun getUserSubscribes(
        userId: Long,
        page: Int = 0,
        size: Int = 10,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<SubscribeResponse>> {
        if (!forceFetch) {
            val cached = memoryCache.get(userId, TYPE_SUBSCRIBES, page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
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
        size: Int = 10,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<SubscribeResponse>> {
        if (!forceFetch) {
            val cached = memoryCache.get(userId, TYPE_SUBSCRIBERS, page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
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
        size: Int = 10,
        forceFetch: Boolean = false
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