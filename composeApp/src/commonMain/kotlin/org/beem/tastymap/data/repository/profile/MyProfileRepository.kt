package org.beem.tastymap.data.repository.profile

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.cache.CacheManager
import org.beem.tastymap.data.cache.ProfileMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.mapper.toDomain
import org.beem.tastymap.data.model.auth.UserResponse
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.data.model.profile.ChangePassword
import org.beem.tastymap.data.model.profile.MessageResponse
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.remote.FileRemoteDataSource
import org.beem.tastymap.data.remote.profile.MyProfileDataSource
import org.beem.tastymap.domain.model.UserProfile

class MyProfileRepository(
    private val dataSource: MyProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource,
    private val userManager: UserManager,
    private val fileRemoteDataSource: FileRemoteDataSource,
    private val cacheManager: CacheManager,
    private val dispatchers: DispatcherProvider
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMyProfile(): Flow<UserProfile?> {
        return userManager.userSession
            .map { it?.userId }
            .distinctUntilChanged()
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf<UserProfile?>(null)
                } else {
                    flow {
                        memoryCache.get(userId)?.let { cachedProfile ->
                            emit(cachedProfile)
                        }

                        emitAll(
                            localDataSource
                                .getProfileFlow(userId) // filterNotNull() KALDIRILDI
                                .distinctUntilChanged()
                                .onEach { profile ->
                                    if (profile != null) {
                                        memoryCache.put(userId, profile)
                                    }
                                }
                        )
                    }
                }
            }
            .flowOn(dispatchers.io)
    }

    suspend fun refreshMyProfile(): ResultWrapper<UserProfile> {
        return try {
            withContext(dispatchers.io) {

                val userId = userManager.userSession.value?.userId

                val remoteDto = dataSource.getUserProfile()
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


    suspend fun getActiveDevices(): ResultWrapper<ActiveDevicesResponse> = withContext(dispatchers.io) {
        safeApiCall { dataSource.getActiveDevices() }
    }

    suspend fun uploadProfilePhoto(file: PlatformFile): ResultWrapper<String> = withContext(dispatchers.io) {
        safeApiCall {
            val response = fileRemoteDataSource.uploadFile(file = file, type = "profiles")
            response.imageUrl
        }
    }


    suspend fun updateProfile(request: UpdateProfile): ResultWrapper<MessageResponse> = withContext(dispatchers.io) {
        val result = safeApiCall { dataSource.updateProfile(request) }

        if (result is ResultWrapper.Success) {
            val myUserId = userManager.userSession.value?.userId
            if (myUserId != null) {
                userManager.updateProfileSession(
                    username = request.username,
                    name = request.name,
                    surname = request.surname,
                    profilePhoto = request.profilePhoto,
                    biography = request.biography
                )

                memoryCache.get(myUserId)?.let { oldProfile ->
                    memoryCache.put(
                        myUserId,
                        oldProfile.copy(
                            username = request.username ?: oldProfile.username,
                            name = request.name ?: oldProfile.name,
                            surname = request.surname ?: oldProfile.surname,
                            profilePhoto = request.profilePhoto ?: oldProfile.profilePhoto,
                            biography = request.biography ?: oldProfile.biography
                        )
                    )
                }

                localDataSource.updatePartialProfile(
                    userId = myUserId,
                    username = request.username,
                    name = request.name,
                    surname = request.surname,
                    profilePhoto = request.profilePhoto,
                    biography = request.biography
                )
            }
        }
        result
    }

    suspend fun changePassword(request: ChangePassword): ResultWrapper<MessageResponse> = withContext(dispatchers.io) {
        safeApiCall { dataSource.changePassword(request) }
    }

    suspend fun getMe(): ResultWrapper<UserResponse> = withContext(dispatchers.io) {
        safeApiCall { dataSource.getMe() }
    }

    suspend fun updatePrivacyStatus(isPrivate: Boolean): ResultWrapper<Unit> = withContext(dispatchers.io) {
        val result = safeApiCall {
            dataSource.updatePrivacyStatus(isPrivate)
        }
        if (result is ResultWrapper.Success) {
            val myUserId = userManager.userSession.value?.userId
            if (myUserId != null) {
                memoryCache.get(myUserId)?.let { oldProfile ->
                    memoryCache.put(
                        myUserId,
                        oldProfile.copy(privateProfile = isPrivate)
                    )
                }

                localDataSource.updatePrivacyStatus(
                    userId = myUserId,
                    isPrivate = isPrivate
                )
            }
        }
        result
    }

    suspend fun logout(deviceId: String): ResultWrapper<Unit> = withContext(dispatchers.io) {
        try {
            safeApiCall { dataSource.logout(deviceId) }
        } finally {
            cacheManager.clearAllMemoryCaches()
        }
    }

    suspend fun getAllUsers(): ResultWrapper<List<UserResponse>> = withContext(dispatchers.io) {
        safeApiCall { dataSource.getAllUsers() }
    }
}