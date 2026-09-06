package org.beem.tastymap.ui.profile.myprofile.notification

import org.beem.tastymap.data.model.socialnotifications.SocialNotificationsResponse

data class SocialNotificationsState(
    val items: List<SocialNotificationsResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val errorMessage: String? = null
)