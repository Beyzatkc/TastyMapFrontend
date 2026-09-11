package org.beem.tastymap.data.repository.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.mapper.toDomain
import org.beem.tastymap.data.remote.profile.ProfileDataSource
import org.beem.tastymap.domain.model.UserProfile

class ProfileRepository(
    private val dataSource: ProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource,
    private val dispatchers: DispatcherProvider
) {

    fun getProfile(userId: Long): Flow<ResultWrapper<UserProfile>> {
        return flow {
            val cachedProfile = memoryCache.get(userId)
            if (cachedProfile != null) {
                emit(ResultWrapper.Success(cachedProfile))
            }

            emitAll(
                localDataSource.getProfileFlow(userId)
                    .filterNotNull()
                    .distinctUntilChanged()
                    .map { dbProfile ->
                        memoryCache.put(userId, dbProfile)
                        ResultWrapper.Success(dbProfile)
                    }
            )
        }
            .onStart {
                CoroutineScope(currentCoroutineContext()).launch {
                    try {
                        fetchRemoteProfile(userId)
                    } catch (e: Exception) {
                        println("[ProfileRepo] ERROR: Asenkron Remote Fetch Hatası -> ${e.message}")
                    }
                }
            }
            .flowOn(dispatchers.io)
    }

    suspend fun fetchRemoteProfile(userId: Long) = withContext(dispatchers.io) {
        val remoteDto = dataSource.getUserProfile(userId)
        val freshProfile = remoteDto.toDomain(userId)

        memoryCache.put(userId, freshProfile)
        localDataSource.saveProfile(freshProfile)
    }
}