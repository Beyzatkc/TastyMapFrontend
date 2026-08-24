package org.beem.tastymap.data.repository.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.mapper.toDomain
import org.beem.tastymap.data.model.profile.ProfileResponse
import org.beem.tastymap.data.remote.profile.ProfileDataSource
import org.beem.tastymap.domain.model.UserProfile

class ProfileRepository(
    private val dataSource: ProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource
) {
     fun getProfile(userId: Long): Flow<ResultWrapper<UserProfile>> = flow {
         val l1Profile = memoryCache.get(userId)
         val l2Profile = if (l1Profile == null) localDataSource.getProfile(userId) else null

         if (l1Profile != null) {
             emit(ResultWrapper.Success(l1Profile))
         }
         else if (l2Profile != null) {
             memoryCache.put(userId, l2Profile)
             emit(ResultWrapper.Success(l2Profile))
         }
        try {
            val remoteDto = dataSource.getUserProfile(userId)
            val freshProfile = remoteDto.toDomain(userId)

            memoryCache.put(userId, freshProfile)
            localDataSource.saveProfile(freshProfile)
            emit(ResultWrapper.Success(freshProfile))
        } catch (e: Exception) {
            if (l1Profile == null && localDataSource.getProfile(userId) == null) {
                emit(ResultWrapper.Error(e.message ?: "Profil bilgileri yüklenemedi.", ErrorType.SERVER_ERROR))
            }
        }
    }

}