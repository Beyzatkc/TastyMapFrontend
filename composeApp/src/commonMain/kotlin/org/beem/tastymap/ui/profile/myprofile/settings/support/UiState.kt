package org.beem.tastymap.ui.profile.myprofile.settings.support

import org.jetbrains.compose.resources.StringResource

data class SupportUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val messageError: StringResource? = null
)