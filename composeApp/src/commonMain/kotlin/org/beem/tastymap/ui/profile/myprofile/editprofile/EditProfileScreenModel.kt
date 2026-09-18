package org.beem.tastymap.ui.profile.myprofile.editprofile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.domain.model.UserProfile
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_profile_success

class EditProfileScreenModel(
    private val repo: MyProfileRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var initialProfile: UserProfile? = null
    private var isFormInitialized = false

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repo.getMyProfile()
                .filterNotNull()
                .collect { profile ->
                    initialProfile = profile

                    _uiState.update { currentState ->
                        if (!isFormInitialized) {
                            isFormInitialized = true
                            currentState.copy(
                                profile = profile,
                                isLoading = false
                            )
                        } else {
                            currentState.copy(isLoading = false)
                        }
                    }
                }
        }
    }

    fun updateProfile(
        inputUsername: String,
        inputName: String,
        inputSurname: String,
        inputBiography: String?,
        selectedFile: PlatformFile? = null
    ) {
        val original = initialProfile ?: return

        val changedUsername = if (inputUsername.trim() != original.username) inputUsername.trim() else null
        val changedName = if (inputName.trim() != original.name) inputName.trim() else null
        val changedSurname = if (inputSurname.trim() != original.surname) inputSurname.trim() else null
        val changedBiography = if (inputBiography?.trim() != original.biography) inputBiography?.trim() else null

        if (!validateForm(
                changedUsername ?: original.username.orEmpty(),
                changedName ?: original.name.orEmpty(),
                changedSurname ?: original.surname.orEmpty()
            )
        ) return

        if (changedUsername == null && changedName == null && changedSurname == null &&
            changedBiography == null && selectedFile == null
        ) {
            _uiState.update { it.copy(successMessageRes = Res.string.edit_profile_success) }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, errorMessage = null, successMessageRes = null) }

            var uploadedPhotoUrl: String? = null

            if (selectedFile != null) {
                when (val uploadResult = repo.uploadProfilePhoto(selectedFile)) {
                    is ResultWrapper.Success -> uploadedPhotoUrl = uploadResult.data
                    is ResultWrapper.Error -> {
                        _uiState.update { it.copy(isActionLoading = false, errorMessage = uploadResult.message) }
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
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            successMessageRes = Res.string.edit_profile_success
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isActionLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun validateForm(username: String, name: String, surname: String): Boolean {
        val uResult = CheckValidator.validateUsername(username.trim())
        val nResult = CheckValidator.validateName(name.trim().replace("\\s+".toRegex(), " "))
        val sResult = CheckValidator.validateSurname(surname.replace("\\s+".toRegex(), " "))

        val usernameError = (uResult as? ValidationResult.Invalid)?.messageRes
        val nameError = (nResult as? ValidationResult.Invalid)?.messageRes
        val surnameError = (sResult as? ValidationResult.Invalid)?.messageRes

        _uiState.update {
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

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessageRes = null,
                usernameError = null,
                nameError = null,
                surnameError = null
            )
        }
    }
}