package org.beem.tastymap.ui.profile.myprofile.editprofile

import io.github.vinceglb.filekit.core.PlatformFile
import org.beem.tastymap.domain.model.UserProfile
import org.jetbrains.compose.resources.StringResource

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessageRes: StringResource? = null,
    val profile: UserProfile? = null,

    val usernameError: StringResource? = null,
    val nameError: StringResource? = null,
    val surnameError: StringResource? = null
)
