package org.beem.tastymap.data.repository.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.mapper.toDomain
import org.beem.tastymap.data.model.auth.UserResponse
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.data.model.profile.ChangePassword
import org.beem.tastymap.data.model.profile.MessageResponse
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.remote.profile.MyProfileDataSource
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.domain.model.UserProfile

class MyProfileRepository(
    private val dataSource: MyProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource,
    private val userManager: UserManager,
    private val clearSessionUseCase: ClearSessionUseCase
) {

    fun getMyProfile(): Flow<ResultWrapper<UserProfile>> = flow {
        val myUserId = userManager.getUserId()
            ?: return@flow emit(ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED))

        val l1Profile = memoryCache.get(myUserId)
        val l2Profile = if (l1Profile == null) localDataSource.getProfile(myUserId) else null

        if (l1Profile != null) {
            emit(ResultWrapper.Success(l1Profile))
        } else if (l2Profile != null) {
            memoryCache.put(myUserId, l2Profile)
            emit(ResultWrapper.Success(l2Profile))
        }

        try {
            val remoteDto = dataSource.getUserProfile()
            val freshProfile = remoteDto.toDomain(myUserId)

            memoryCache.put(myUserId, freshProfile)
            localDataSource.saveProfile(freshProfile)

            emit(ResultWrapper.Success(freshProfile))
        } catch (e: Exception) {
            if (l1Profile == null && l2Profile == null) {
                emit(ResultWrapper.Error(e.message ?: "Profil yüklenemedi.", ErrorType.SERVER_ERROR))
            }else {
                emit(ResultWrapper.Error(e.cause.toString() + e.message, ErrorType.UNKNOWN_ERROR))
            }
        }
    }

    // BUUNA BAKILCAK
    suspend fun getActiveDevices(): ResultWrapper<ActiveDevicesResponse> {
        return safeApiCall {
            dataSource.getActiveDevices()
        }
    }

    suspend fun updateProfile(request: UpdateProfile): ResultWrapper<MessageResponse> {
        val result = safeApiCall { dataSource.updateProfile(request) }

        if (result is ResultWrapper.Success) {
            val myUserId = userManager.getUserId()
            if (myUserId != null) {
                userManager.updateProfileSession(
                    username = request.username,
                    name = request.name,
                    surname = request.surname,
                    profilePhoto = request.profilePhoto,
                    biography = request.biography
                )

              //ramden guncelleıdk
                memoryCache.get(myUserId)?.let { oldProfile ->
                    memoryCache.put(
                        myUserId,
                        oldProfile.copy(
                            username = request.username,
                            name = request.name,
                            profilePhoto = request.profilePhoto ?: oldProfile.profilePhoto,
                            biography = request.biography ?: oldProfile.biography
                        )
                    )
                }

              //sqldelıghtdan guncelledık
                localDataSource.getProfile(myUserId)?.let { oldProfile ->
                    localDataSource.saveProfile(
                        oldProfile.copy(
                            username = request.username,
                            name = request.name,
                            profilePhoto = request.profilePhoto ?: oldProfile.profilePhoto,
                            biography = request.biography ?: oldProfile.biography
                        )
                    )
                }
            }
        }
        return result
    }

    suspend fun changePassword(request: ChangePassword): ResultWrapper<MessageResponse> {
        return safeApiCall { dataSource.changePassword(request) }
    }

    suspend fun getMe(): ResultWrapper<UserResponse> {
        return safeApiCall {
            dataSource.getMe()
        }
    }

    suspend fun logout(deviceId: String): ResultWrapper<Unit> {
        return try {
            safeApiCall { dataSource.logout(deviceId) }
        } finally {
            clearSessionUseCase()
        }
    }
}