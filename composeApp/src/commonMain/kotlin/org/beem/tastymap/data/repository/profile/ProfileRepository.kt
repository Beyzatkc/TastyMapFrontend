package org.beem.tastymap.data.repository.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.mapper.toDomain
import org.beem.tastymap.data.remote.profile.ProfileDataSource
import org.beem.tastymap.domain.model.UserProfile

class ProfileRepository(
    private val dataSource: ProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource
) {

    /*
    fun getProfile(userId: Long): Flow<ResultWrapper<UserProfile>> = flow {
        val l1Profile = memoryCache.get(userId)
        val l2Profile = if (l1Profile == null) localDataSource.getProfile(userId) else null

        if (l1Profile != null) {
            emit(ResultWrapper.Success(l1Profile))
        } else if (l2Profile != null) {
            memoryCache.put(userId, l2Profile)
            emit(ResultWrapper.Success(l2Profile))
        }

        try {
            val remoteDto = dataSource.
            getUserProfile(userId)

            val freshProfile = remoteDto.toDomain(userId)

            memoryCache.put(userId, freshProfile)
            localDataSource.saveProfile(freshProfile)
            emit(ResultWrapper.Success(freshProfile))
        } catch (e: Exception) {
            val errorMessage =
                e.message ?: e.cause?.message ?: "Profil güncellenirken bir hata oluştu."

            if (l1Profile == null && l2Profile == null) {
                emit(ResultWrapper.Error(errorMessage, ErrorType.SERVER_ERROR))
            } else {
                emit(ResultWrapper.Error(errorMessage, ErrorType.UNKNOWN_ERROR))
            }
        }
    }

     */

    fun getMyProfile(userId: Long): Flow<ResultWrapper<UserProfile>> {
        return flow {
            // 1. Önce L1 Memory Cache kontrol edilir, varsa hemen emit edilir
            val cachedProfile = memoryCache.get(userId)
            if (cachedProfile != null) {
                emit(ResultWrapper.Success(cachedProfile))
            }

            // 2. Ardından DB (LocalDataSource) akışına abone olunur
            // DB değiştiğinde yeni veri otomatik olarak emit edilecektir
            emitAll(
                localDataSource.getProfileFlow(userId)
                    .filterNotNull() // <--- NULL (boş) veriyi filtrelerez. Ekrana hata fırlatmayı engeller!
                    .map { dbProfile ->
                        println("PROFILE_FLOW [L2 - DB]: Veritabanından yeni veri geldi ve L1 Cache güncellendi -> $dbProfile")
                        memoryCache.put(userId, dbProfile)
                        ResultWrapper.Success(dbProfile)
                    }
            )
        }
            .onStart {
                // L1 önbellekte veri varsa ilk render hızını artırmak için L1'i emit edebiliriz,
                // ancak arka planda güncel API verisini çekeriz.
                    fetchRemoteProfile(userId)
            }
    }

    suspend fun fetchRemoteProfile(userId: Long) {
        try {
            val remoteDto = dataSource.getUserProfile(userId)
            val freshProfile = remoteDto.toDomain(userId)

            memoryCache.put(userId, freshProfile)
            localDataSource.saveProfile(freshProfile)
        } catch (e: Exception) {
            println("FLOW"+e)
            throw e // Gerekirse UI'da hata göstermek için hatayı fırlatabilirsin
        }
    }
}