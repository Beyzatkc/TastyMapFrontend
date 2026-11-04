package org.beem.tastymap.ui.profile.myprofile.settings.changepassword

import org.jetbrains.compose.resources.StringResource

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val successMessageRes: StringResource? = null,
    val errorMessage: String? = null,

    val oldPasswordError: StringResource? = null,
    val newPasswordError: StringResource? = null,
    val againNewPasswordError: StringResource? = null,
)