package org.beem.tastymap.ui.profile.myprofile.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.provider.DeviceInfoProvider

import org.beem.tastymap.data.repository.profile.MyProfileRepository

class SettingsScreenModel(
    private val repo: MyProfileRepository,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val settingsManager: SettingsManager
) : ScreenModel {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()
    val isDarkMode: StateFlow<Boolean?> = settingsManager.isDarkMode

    val languageCode = settingsManager.languageCode


    fun setInitialPrivacyStatus(initialStatus: Boolean) {
        _uiState.update { it.copy(isAccountPrivate = initialStatus) }
    }
    fun setLanguage(code: String) {
        settingsManager.setLanguageCode(code)
    }

    fun toggleDarkMode(enabled: Boolean) {
        settingsManager.setDarkMode(enabled)
    }

    fun logout() {
        screenModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            val deviceId = deviceInfoProvider.getDeviceId()
            repo.logout(deviceId)
            _uiState.update { it.copy(isActionLoading = false,isLoggedOut = true) }
        }
    }
    fun updatePrivacyStatus(isPrivate: Boolean) {
        screenModelScope.launch {
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