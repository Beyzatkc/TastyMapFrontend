package org.beem.tastymap.ui.profile.myprofile

import org.beem.tastymap.data.model.profile.ActiveDeviceDTO
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.domain.model.UserProfile
data class MyProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null,
    val isActionLoading: Boolean = false,
    val successMessage: String? = null
)