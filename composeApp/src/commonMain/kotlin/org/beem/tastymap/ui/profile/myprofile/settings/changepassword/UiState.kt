package org.beem.tastymap.ui.profile.myprofile.settings.changepassword

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,

    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val againNewPasswordError: String? = null
)