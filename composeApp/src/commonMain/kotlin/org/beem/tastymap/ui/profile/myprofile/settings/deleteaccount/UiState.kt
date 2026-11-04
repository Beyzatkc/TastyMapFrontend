package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

data class DeleteAccountUiState(
    val isDeleteLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val passwordError: String? = null,
    val errorMessage: String? = null
)