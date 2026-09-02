package org.beem.tastymap.ui.profile.myprofile

import org.beem.tastymap.data.model.profile.ActiveDeviceDTO
import org.beem.tastymap.data.model.profile.ActiveDevicesResponse
import org.beem.tastymap.domain.model.UserProfile
import org.jetbrains.compose.resources.StringResource

data class MyProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null,
    val isActionLoading: Boolean = false,
    val successMessageRes: StringResource? = null,

    val usernameInput: String = "",
    val nameInput: String = "",
    val surnameInput: String = "",
    val bioInput: String = "",


    val usernameError: StringResource? = null,
    val nameError: StringResource? = null,
    val surnameError: StringResource? = null,
)