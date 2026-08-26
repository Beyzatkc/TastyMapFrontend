package org.beem.tastymap.ui.profile.otherprofile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.profile.ProfileRepository
import org.beem.tastymap.domain.model.UserProfile


class ProfileScreenModel(
    private val repo: ProfileRepository,
): ScreenModel {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState = _profileState.asStateFlow()

    fun getProfile2(userId: Long) {
        // Backend/API tam hazır olana kadar veya test için:
        _profileState.value = ProfileUiState(
            isLoading = false,
            profile = UserProfile(
                userId = userId,
                username = "gurme_ahmet",
                name = "Ahmet Yılmaz",
                profilePhoto = null,
                role = "GURME",
                biography = "İstanbul lezzet haritasını çıkaran sokak gurmesi 🍕🍔",
                postCount = 42,
                subscriberCount = 1250,
                subscribedCount = 380,
                blockedByMe = false,
                blockedMe = false
            )
        )
    }
    fun getProfile(userId: Long) {
        screenModelScope.launch {
            _profileState.update { it.copy(isLoading = true, errorMessage = null) }

            repo.getProfile(userId).collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is ResultWrapper.Error -> {
                        _profileState.update {
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
}