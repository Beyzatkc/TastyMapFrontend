package org.beem.tastymap.ui.profile.myprofile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult
import org.beem.tastymap.ui.common.NotificationBadgeManager
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_profile_success

class MyProfileScreenModel(
    private val repo: MyProfileRepository,
    private val badgeManager: NotificationBadgeManager
) : ScreenModel {

    val hasUnreadBadge = badgeManager.hasUnreadBadge
    private val _myProfileState = MutableStateFlow(MyProfileUiState())
    val myProfileState = _myProfileState.asStateFlow()

    private var profileJob: Job? = null


    fun getMyProfile() {
        if (profileJob?.isActive == true) return

        profileJob = screenModelScope.launch {
            _myProfileState.update {
                it.copy(isLoading = it.profile == null, errorMessage = null)
            }

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

    fun refreshProfile() {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isRefreshing = true, errorMessage = null) }

            try {
                repo.fetchRemoteProfile()
                _myProfileState.update { it.copy(isRefreshing = false) }

            } catch (e: Exception) {
                _myProfileState.update {
                    it.copy(isRefreshing = false, errorMessage = e.message ?: "Yenilenirken bir hata oluştu")
                }
            }
        }
    }

    /*
    fun getMyProfile(isFromPullToRefresh: Boolean = false) {
        screenModelScope.launch {
            _myProfileState.update {
                if (isFromPullToRefresh) {
                    it.copy(isRefreshing = true, errorMessage = null)
                } else {
                    it.copy(isLoading = it.profile == null, errorMessage = null)
                }
            }

            repo.getMyProfile()
                .onCompletion {
                    _myProfileState.update { it.copy(isRefreshing = false, isLoading = false) }
                }
                .collect { result ->
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

     */

    fun updateProfile(
        inputUsername: String,
        inputName: String,
        inputSurname: String,
        inputBiography: String?,
        selectedFile: PlatformFile? = null
    ) {
        val currentProfile = _myProfileState.value.profile ?: return

        val changedUsername = if (inputUsername.trim() != currentProfile.username) inputUsername.trim() else null
        val changedName = if (inputName.trim() != currentProfile.name) inputName.trim() else null
        val changedSurname = if (inputSurname.trim() != currentProfile.surname) inputSurname.trim() else null
        val changedBiography = if (inputBiography?.trim() != currentProfile.biography) inputBiography?.trim() else null

        val isValid = validateUpdateState(
            changedUsername ?: currentProfile.username,
            changedName ?: currentProfile.name,
            changedSurname ?: currentProfile.surname
        )

        if (!isValid) return

        if (changedUsername == null && changedName == null && changedSurname == null &&
            changedBiography == null && selectedFile == null
        ) {
            _myProfileState.update {
                it.copy(successMessageRes = Res.string.edit_profile_success)
            }
            return
        }

        screenModelScope.launch {
            _myProfileState.update {
                it.copy(
                    isActionLoading = true,
                    errorMessage = null,
                    successMessageRes = null
                )
            }
            var uploadedPhotoUrl: String? = null

            if (selectedFile != null) {
                when (val uploadResult = repo.uploadProfilePhoto(selectedFile)) {
                    is ResultWrapper.Success -> {
                        uploadedPhotoUrl = uploadResult.data
                    }
                    is ResultWrapper.Error -> {
                        _myProfileState.update {
                            it.copy(
                                isActionLoading = false,
                                errorMessage = uploadResult.message
                            )
                        }
                        return@launch
                    }
                }
            }

            val patchRequest = UpdateProfile(
                username = changedUsername,
                name = changedName,
                surname = changedSurname,
                biography = changedBiography,
                profilePhoto = uploadedPhotoUrl
            )

            when (val result = repo.updateProfile(patchRequest)) {
                is ResultWrapper.Success -> {
                    _myProfileState.update { currentState ->
                        val updatedProfile = currentState.profile?.copy(
                            username = patchRequest.username ?: currentState.profile.username,
                            name = patchRequest.name ?: currentState.profile.name,
                            surname = patchRequest.surname ?: currentState.profile.surname,
                            profilePhoto = patchRequest.profilePhoto ?: currentState.profile.profilePhoto,
                            biography = patchRequest.biography ?: currentState.profile.biography
                        )
                        currentState.copy(
                            isActionLoading = false,
                            profile = updatedProfile,
                            successMessageRes = Res.string.edit_profile_success
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
    fun validateUpdateState(username: String, name: String, surname: String): Boolean {
        val uResult = CheckValidator.validateUsername(username.trim())
        val nResult = CheckValidator.validateName(name.trim().replace("\\s+".toRegex(), " "))
        val sResult = CheckValidator.validateSurname(surname.replace("\\s+".toRegex(), " "))

        val usernameError = (uResult as? ValidationResult.Invalid)?.messageRes
        val nameError = (nResult as? ValidationResult.Invalid)?.messageRes
        val surnameError = (sResult as? ValidationResult.Invalid)?.messageRes

        _myProfileState.update {
            it.copy(
                usernameError = usernameError,
                nameError = nameError,
                surnameError = surnameError
            )
        }

        return uResult is ValidationResult.Valid &&
                nResult is ValidationResult.Valid &&
                sResult is ValidationResult.Valid
    }

    fun clearMessagesProfile() {
        _myProfileState.update { it.copy(errorMessage = null, successMessageRes = null) }
    }

    fun clearMessagesEdit() {
        _myProfileState.update {
            it.copy(
                errorMessage = null,
                successMessageRes = null,
                usernameError = null,
                nameError = null,
                surnameError = null
            )
        }
    }

    fun getAllUsers() {
        screenModelScope.launch {
            _myProfileState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repo.getAllUsers()) {
                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isLoading = false,
                            DENEME = result.data, // UiState modelinizde "usersList" alanını günceller
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