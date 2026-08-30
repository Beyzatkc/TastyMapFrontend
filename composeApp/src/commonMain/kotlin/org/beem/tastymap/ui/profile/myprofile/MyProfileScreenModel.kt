package org.beem.tastymap.ui.profile.myprofile
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.profile.UpdateProfile
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult

class MyProfileScreenModel(
    private val repo: MyProfileRepository
): ScreenModel{

    private val _myProfileState = MutableStateFlow(MyProfileUiState())
    val myProfileState = _myProfileState.asStateFlow()


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

    fun updateProfile(request: UpdateProfile) {
        val isValid = validateUpdateState(request.username, request.name, request.surname)
        if (!isValid) return

        screenModelScope.launch {
            _myProfileState.update { it.copy(isActionLoading = true, errorMessage = null, successMessage = null) }

            when (val result = repo.updateProfile(request)) {
                is ResultWrapper.Success -> {
                    _myProfileState.update { currentState ->
                        val updatedProfile = currentState.profile?.copy(
                            username = request.username.trim(),
                            name = request.name.trim(),
                            profilePhoto = request.profilePhoto ?: currentState.profile.profilePhoto,
                            biography = request.biography?.trim() ?: currentState.profile.biography
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

    fun validateUpdateState(username: String,name: String, surname:String): Boolean {
        val uResult = CheckValidator.validateUsername(username.trim())
        val nResult = CheckValidator.validateName(name.trim().replace("\\s+".toRegex(), " "))
        val sResult = CheckValidator.validateSurname(surname.replace("\\s+".toRegex(), " "))

        val usernameError = (uResult as? ValidationResult.Invalid)?.message
        val nameError = (nResult as? ValidationResult.Invalid)?.message
        val surnameError = (sResult as? ValidationResult.Invalid)?.message

        _myProfileState.update {
            it.copy(
                usernameError = usernameError,
                surnameError = surnameError,
                nameError = nameError
            )
        }

        return uResult is ValidationResult.Valid &&
                nResult is ValidationResult.Valid &&
                sResult is ValidationResult.Valid

    }

    // Toast/Snackbar gösterildikten sonra mesajları temizlemek için yardımcı fonksiyon
    fun clearMessagesProfile() {
        _myProfileState.update { it.copy(errorMessage = null, successMessage = null) }

    }
    fun clearMessagesEdit() {
        _myProfileState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
                usernameError = null,
                nameError = null,
                surnameError = null
            )
        }
    }
}