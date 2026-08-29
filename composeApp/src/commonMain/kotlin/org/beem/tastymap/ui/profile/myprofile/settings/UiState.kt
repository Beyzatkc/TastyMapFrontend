package org.beem.tastymap.ui.profile.myprofile.settings

import org.beem.tastymap.data.model.profile.ActiveDeviceDTO

data class SettingsUiState(
    val isActionLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null
)
