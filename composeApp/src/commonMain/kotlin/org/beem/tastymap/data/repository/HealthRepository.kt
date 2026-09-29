package org.beem.tastymap.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.cache.HealthMemoryCache
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.HealthLocalDataSource
import org.beem.tastymap.data.model.health.HealthRequest
import org.beem.tastymap.data.model.health.HealthResponse
import org.beem.tastymap.data.remote.HealthDataSource

class HealthRepository(
    private val dataSource: HealthDataSource,
    private val localDataSource: HealthLocalDataSource,
    private val memoryCache: HealthMemoryCache,
    private val userManager: UserManager,
    private val dispatchers: DispatcherProvider
) {
    val myId: Long
        get() = userManager.userSession.value?.userId ?: 0L

    fun observeHealth(): Flow<HealthResponse?> {
        return localDataSource.getHealthInfo(myId)
            .distinctUntilChanged()
            .flowOn(dispatchers.io)
    }

    suspend fun addHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        val result = safeApiCall { dataSource.addHealth(request) }
        if (result is ResultWrapper.Success) {
            userManager.setOnBoardComplete(true)
            val myUserId = userManager.userSession.value?.userId
            if (myUserId != null) {
                memoryCache.put(myUserId, result.data)
                localDataSource.saveHealthInfo(myUserId, result.data)
            }
        }
        return result
    }
    suspend fun updateHealth(request: HealthRequest): ResultWrapper<HealthResponse> {
        val result = safeApiCall { dataSource.updateHealth(request) }
        if (result is ResultWrapper.Success) {
            val myUserId = userManager.userSession.value?.userId
            if (myUserId != null) {
                memoryCache.put(myUserId, result.data)
                localDataSource.updateHealthPartiallyLocally(myUserId, result.data.hasDiabetes,result.data.eatType,result.data.allergyInfo)
            }
        }
        return result
    }
     fun getHealth(): Flow<ResultWrapper<HealthResponse>> = flow {

        val cachedHealth = memoryCache.get(myId)
        if (cachedHealth != null) {
            emit(ResultWrapper.Success(cachedHealth))
        }

        when (val remoteResult = safeApiCall { dataSource.getHealth() }) {
            is ResultWrapper.Success -> {
                memoryCache.put(myId, remoteResult.data)
                localDataSource.saveHealthInfo(myId, remoteResult.data)

                emit(remoteResult)
            }
            is ResultWrapper.Error -> {
                emit(remoteResult)
            }
        }
    }
}