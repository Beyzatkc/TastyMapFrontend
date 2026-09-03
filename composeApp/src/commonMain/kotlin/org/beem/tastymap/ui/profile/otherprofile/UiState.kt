package org.beem.tastymap.ui.profile.otherprofile

import org.beem.tastymap.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isActionLoading: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null
)