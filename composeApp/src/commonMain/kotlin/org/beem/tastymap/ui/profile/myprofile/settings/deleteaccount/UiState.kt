package org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount

data class DeleteAccountUiState(
    val isDeleteLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)