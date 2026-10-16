package org.beem.tastymap.data.repository.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.network.ErrorType
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

    fun getProfile(userId: Long): Flow<UserProfile> {
        return flow {

            memoryCache.get(userId)?.let { cachedProfile ->
                emit(cachedProfile)
            }

            emitAll(
                localDataSource
                    .getProfileFlow(userId)
                    .filterNotNull()
                    .distinctUntilChanged()
                    .map { dbProfile ->
                        memoryCache.put(userId, dbProfile)
                        dbProfile
                    }
            )
        }.flowOn(dispatchers.io)
    }

    suspend fun refreshProfile(userId: Long): ResultWrapper<UserProfile> {
        return try {
            withContext(dispatchers.io) {

                val remoteDto = dataSource.getUserProfile(userId)
                val freshProfile = remoteDto.toDomain(userId)

                memoryCache.put(userId, freshProfile)
                localDataSource.saveProfile(freshProfile)

                ResultWrapper.Success(freshProfile)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(
                e.message.toString(), ErrorType.UNKNOWN_ERROR
            )
        }
    }
}