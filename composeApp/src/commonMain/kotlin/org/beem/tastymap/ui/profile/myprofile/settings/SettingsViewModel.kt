package org.beem.tastymap.ui.profile.myprofile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider

import org.beem.tastymap.data.repository.profile.MyProfileRepository
class SettingsViewModel(
    private val repo: MyProfileRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val settingsManager: SettingsManager,
    private val authEventBus: AuthEventBus
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()
    val isDarkMode: StateFlow<Boolean?> = settingsManager.isDarkMode

    val languageCode = settingsManager.languageCode


    init {
        observePrivacyStatus()
    }

    private fun observePrivacyStatus() {
        viewModelScope.launch {
            repo.getPrivacyStatus().collect { isPrivate ->
                if (isPrivate != null) {
                    _uiState.update {
                        it.copy(
                            isAccountPrivate = isPrivate,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = true) }
                    repo.refreshMyProfile()
                }
            }
        }
    }
    fun setLanguage(code: String) {
        settingsManager.setLanguageCode(code)
    }

    fun toggleDarkMode(enabled: Boolean) {
        settingsManager.setDarkMode(enabled)
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            val deviceId = deviceInfoProvider.getDeviceId()
            repo.logout(deviceId)
            _uiState.update { it.copy(isActionLoading = false,isLoggedOut = true) }

            authEventBus.emit(AuthEventBus.AuthEvent.OnLoggedOut)
        }
    }
    fun updatePrivacyStatus(isPrivate: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPrivacyLoading = true) }

            when (val result = repo.updatePrivacyStatus(isPrivate)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isAccountPrivate = isPrivate,
                            isPrivacyLoading = false
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isPrivacyLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}