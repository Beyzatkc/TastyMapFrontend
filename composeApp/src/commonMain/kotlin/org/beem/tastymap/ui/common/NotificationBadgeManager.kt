package org.beem.tastymap.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationBadgeManager {
    private val _hasUnreadBadge = MutableStateFlow(false)
    val hasUnreadBadge = _hasUnreadBadge.asStateFlow()

    fun updateBadge(hasUnread: Boolean) {
        _hasUnreadBadge.value = hasUnread
    }
}