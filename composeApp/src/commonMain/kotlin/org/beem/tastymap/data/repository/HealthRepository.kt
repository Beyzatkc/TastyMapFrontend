package org.beem.tastymap.data.repository

import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.HealthMemoryCache
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.model.health.HealthRequest
import org.beem.tastymap.data.model.health.HealthResponse
import org.beem.tastymap.data.remote.HealthDataSource

class HealthRepository(
    private val dataSource: HealthDataSource,
    private val memoryCache: HealthMemoryCache,
    private val userManager: UserManager,
) {
    suspend fun addHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        val result = safeApiCall { dataSource.addHealth(request) }
        if (result is ResultWrapper.Success) {
            val myUserId = userManager.getUserId()
            if (myUserId != null) {
                memoryCache.put(myUserId, result.data)
            }
        }
        return result
    }
    suspend fun updateHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        val result = safeApiCall { dataSource.updateHealth(request) }
        if (result is ResultWrapper.Success) {
            val myUserId = userManager.getUserId()
            if (myUserId != null) {
                memoryCache.put(myUserId, result.data)
            }
        }
        return result
    }
    suspend fun getHealth(): ResultWrapper<HealthResponse> {
        val myUserId = userManager.getUserId()
            ?: return ResultWrapper.Error(
                message = "Kullanıcı oturumu bulunamadı.",
                type = ErrorType.UNAUTHORIZED
            )

        val cachedHealth = memoryCache.get(myUserId)
        if (cachedHealth != null) {
            return ResultWrapper.Success(cachedHealth)
        }

        val result = safeApiCall {
            dataSource.getHealth()
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(myUserId, result.data)
        }

        return result
    }
}