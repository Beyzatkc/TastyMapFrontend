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
import org.beem.tastymap.data.repository.SocialNotificationsRepository
import org.beem.tastymap.data.repository.profile.MyProfileRepository
import org.beem.tastymap.ui.auth.common.CheckValidator
import org.beem.tastymap.ui.auth.common.ValidationResult
import org.beem.tastymap.ui.common.NotificationBadgeManager
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.edit_profile_success

class MyProfileScreenModel(
    private val repo: MyProfileRepository,
    private val socialNotificationsRepository: SocialNotificationsRepository,
    private val badgeManager: NotificationBadgeManager
) : ScreenModel {

    val hasUnreadBadge = badgeManager.hasUnreadBadge
    private val _myProfileState = MutableStateFlow(MyProfileUiState())
    val myProfileState = _myProfileState.asStateFlow()

    private var profileJob: Job? = null

    init {
        checkUnreadNotifications()
    }

    private fun checkUnreadNotifications() {
        if (badgeManager.hasUnreadBadge.value) {
            return
        }

        screenModelScope.launch {
            when (val result = socialNotificationsRepository.checkHasUnread()) {
                is ResultWrapper.Success -> {
                    badgeManager.updateBadge(result.data)
                }

                is ResultWrapper.Error -> {
                    println("NOTİFİCATİON " + result.message)
                }
            }
        }
    }

    fun getMyProfile() {
        profileJob?.cancel()

        profileJob = screenModelScope.launch {

            repo.getMyProfile().collect { profile ->

                _myProfileState.update {
                    it.copy(
                        profile = profile,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun fetchRemoteProfile(){
        screenModelScope.launch {
            when (val result = repo.refreshMyProfile()) {

                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
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
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun refreshMyProfile() {
        if (_myProfileState.value.isRefreshing) return

        screenModelScope.launch {

            _myProfileState.update {
                it.copy(isRefreshing = true)
            }

            when (val result = repo.refreshMyProfile()) {

                is ResultWrapper.Success -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
                            profile = result.data,
                            errorMessage = null
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _myProfileState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearMessagesProfile() {
        _myProfileState.update { it.copy(errorMessage = null, successMessageRes = null) }
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