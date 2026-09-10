package org.beem.tastymap.data.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.BlockedMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.block.BlockResponse
import org.beem.tastymap.data.remote.BlockDataSource
import org.beem.tastymap.domain.model.RelationStatus

class BlockRepository(
    private val dataSource: BlockDataSource,
    private val localDataSource: ProfileLocalDataSource,
    private val memoryCache: BlockedMemoryCache
) {
    suspend fun blockUser(userId: Long,myUserId: Long, currentRelationStatus: RelationStatus, isFollower: Boolean): ResultWrapper<Unit>{
        val result = safeApiCall { dataSource.blockUser(userId) }
        if (result is ResultWrapper.Success) {
            localDataSource.blockUserInLocal(userId)

            if (currentRelationStatus == RelationStatus.FOLLOWING) {
                localDataSource.decrementSubscribed(myUserId) // Takip ettiğim sayısını -1 yap
            }
            if (isFollower) {
                localDataSource.decrementSubscriber(myUserId) // Takipçi sayımı -1 yap
            }
            memoryCache.clear()

        }
        return result
    }
    suspend fun unblockUser(userId: Long): ResultWrapper<Unit> {
        val result = safeApiCall { dataSource.unBlockUser(userId) }

        if (result is ResultWrapper.Success) {
            localDataSource.unblockUserInLocal(userId)
        }
        memoryCache.clear()

        return result
    }

    suspend fun getBlockedUsers(
        page: Int = 0,
        size: Int = 10,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<BlockResponse>>{
        if (!forceFetch) {
            val cached = memoryCache.get(page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
        }

        val result = safeApiCall {
            dataSource.getBlockedUsers(page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(page, result.data)
        }

        return result
    }

}