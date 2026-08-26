package org.beem.tastymap.ui.profile.myprofile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider
import org.beem.tastymap.data.model.auth.PasswordRequest
import org.beem.tastymap.data.model.profile.ChangePassword
import org.beem.tastymap.data.model.profile.RefreshTokenRequest
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.repository.profile.MyProfileRepository


class MyProfileScreenModel(
    private val repo: MyProfileRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
): ScreenModel{

    private val _myProfileState = MutableStateFlow(MyProfileUiState())
    val myProfileState = _myProfileState.asStateFlow()

    fun getMyProfile() {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isLoading = true, errorMessage = null) }

            repo.getMyProfile().collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        _myProfileState.update {
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        _myProfileState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateProfile(request: UpdateProfile) {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isActionLoading = true, errorMessage = null, successMessage = null) }

            when (val result = repo.updateProfile(request)) {
                is ResultWrapper.Success -> {
                    _myProfileState.update { currentState ->
                        val updatedProfile = currentState.profile?.copy(
                            username = request.username,
                            name = request.name,
                            profilePhoto = request.profilePhoto ?: currentState.profile.profilePhoto,
                            biography = request.biography ?: currentState.profile.biography
                        )
                        currentState.copy(
                            isActionLoading = false,
                            profile = updatedProfile,
                            successMessage = result.data.message ?: "Profil başarıyla güncellendi."
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _myProfileState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun changePassword(request: ChangePassword) {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isActionLoading = true, errorMessage = null, successMessage = null) }

            when (val result = repo.changePassword(request)) {
                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isActionLoading = false,
                            successMessage = result.data.message ?: "Şifreniz başarıyla değiştirildi."
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _myProfileState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun getActiveDevices() {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isDevicesLoading = true, errorMessage = null) }
            when (val result = repo.getActiveDevices()) {
                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isDevicesLoading = false,
                            activeDeviceCount = result.data.activeDeviceCount,
                            activeDevices = result.data.devices
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _myProfileState.update {
                        it.copy(
                            isDevicesLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun logout(refreshToken: String) {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isActionLoading = true) }

            val dto = RefreshTokenRequest(
                refreshToken = refreshToken,
                deviceId = deviceInfoProvider.getDeviceId()
            )
            repo.logout(dto)

            _myProfileState.update { it.copy(isActionLoading = false) }
        }
    }
    // Toast/Snackbar gösterildikten sonra mesajları temizlemek için yardımcı fonksiyon
    fun clearMessages() {
        _myProfileState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}