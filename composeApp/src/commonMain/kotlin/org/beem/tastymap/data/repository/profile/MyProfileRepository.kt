package org.beem.tastymap.data.repository.profile

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.DispatcherProvider
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
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.domain.model.UserProfile

class MyProfileRepository(
    private val dataSource: MyProfileDataSource,
    private val memoryCache: ProfileMemoryCache,
    private val localDataSource: ProfileLocalDataSource,
    private val userManager: UserManager,
    private val fileRemoteDataSource: FileRemoteDataSource,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val dispatchers: DispatcherProvider
) {

    fun getMyProfile(): Flow<ResultWrapper<UserProfile>> {
        return flow {
            val myUserId = userManager.getUserId()
            if (myUserId == null) {
                emit(ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED))
                return@flow
            }
            val cachedProfile = memoryCache.get(myUserId)
            if (cachedProfile != null) {
                emit(ResultWrapper.Success(cachedProfile))
            }

            emitAll(
                localDataSource.getProfileFlow(myUserId)
                    .filterNotNull()
                    .distinctUntilChanged()
                    .map { dbProfile ->
                        memoryCache.put(myUserId, dbProfile)
                        ResultWrapper.Success(dbProfile)
                    }
            )
        }
            .onStart {
                CoroutineScope(currentCoroutineContext()).launch {
                    try {
                        fetchRemoteProfile()
                    } catch (e: Exception) {
                        println("[ProfileRepo] ERROR: Asenkron Remote Fetch Hatası -> ${e.message}")
                    }
                }
            }
            .flowOn(dispatchers.io)
    }

    suspend fun fetchRemoteProfile() = withContext(dispatchers.io) {
        val myUserId = userManager.getUserId()

        if (myUserId == null) {
            return@withContext
        }
        val remoteDto = dataSource.getUserProfile()
        val freshProfile = remoteDto.toDomain(myUserId)
        memoryCache.put(myUserId, freshProfile)

        localDataSource.saveProfile(freshProfile)
    }


    suspend fun getActiveDevices(): ResultWrapper<ActiveDevicesResponse> = withContext(dispatchers.io) {
        safeApiCall { dataSource.getActiveDevices() }
    }

    suspend fun uploadProfilePhoto(file: PlatformFile): ResultWrapper<String> = withContext(dispatchers.io) {
        safeApiCall {
            val response = fileRemoteDataSource.uploadFile(file)
            response.imageUrl
        }
    }

    suspend fun updateProfile(request: UpdateProfile): ResultWrapper<MessageResponse> = withContext(dispatchers.io) {
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
            val myUserId = userManager.getUserId()
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
            clearSessionUseCase()
        }
    }

    suspend fun getAllUsers(): ResultWrapper<List<UserResponse>> = withContext(dispatchers.io) {
        safeApiCall { dataSource.getAllUsers() }
    }
}