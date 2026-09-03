package org.beem.tastymap.data.repository.profile

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
    private val clearSessionUseCase: ClearSessionUseCase
) {

    /*
    fun getMyProfile(): Flow<ResultWrapper<UserProfile>> = flow {
        val myUserId = userManager.getUserId()
        if (myUserId == null) {
            emit(ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED))
            return@flow
        }

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
            val errorMessage = e.message ?: e.cause?.message ?: "Profil güncellenirken bir hata oluştu."

            if (l1Profile == null && l2Profile == null) {
                emit(ResultWrapper.Error(errorMessage, ErrorType.SERVER_ERROR))
            } else {
                emit(ResultWrapper.Error(errorMessage, ErrorType.UNKNOWN_ERROR))
            }
        }
    }

     */
    fun getMyProfile(): Flow<ResultWrapper<UserProfile>> {
        val myUserId = userManager.getUserId()
            ?: return flowOf(ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED))

        return localDataSource.getProfileFlow(myUserId)
            .map { dbProfile ->
                if (dbProfile != null) {
                    // Veritabanında değişim oldukça L1 Memory Cache'i de güncelliyoruz
                    memoryCache.put(myUserId, dbProfile)
                    ResultWrapper.Success(dbProfile)
                } else {
                    ResultWrapper.Error("Profil verisi bulunamadı.", ErrorType.EMPTY_RESPONSE)
                }
            }
            .onStart {
                // L1 önbellekte veri varsa ilk render hızını artırmak için L1'i emit edebiliriz,
                // ancak arka planda güncel API verisini çekeriz.
                fetchRemoteProfile(myUserId)
            }
    }

    private suspend fun fetchRemoteProfile(myUserId: Long) {
        try {
            val remoteDto = dataSource.getUserProfile()
            val freshProfile = remoteDto.toDomain(myUserId)

            // Veritabanına kaydetmek, getProfileFlow'un otomatik olarak tetiklenip
            // güncel veriyi yeni bir State/Emit olarak fırlatmasını sağlar.
            memoryCache.put(myUserId, freshProfile)
            localDataSource.saveProfile(freshProfile)
        } catch (_: Exception) {
            // Hata durumunda veritabanındaki (offline/mevcut) veri Flow üzerinden aktarılmaya devam eder.
        }
    }


    suspend fun getActiveDevices(): ResultWrapper<ActiveDevicesResponse> {
        return safeApiCall {
            dataSource.getActiveDevices()
        }
    }

    suspend fun uploadProfilePhoto(file: PlatformFile): ResultWrapper<String> {
        return safeApiCall {
            val response = fileRemoteDataSource.uploadFile(file)
            response.imageUrl
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