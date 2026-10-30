package org.beem.tastymap.ui.profile.common

import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
data class ConnectionsDialogState(
    val userId: Long,
    val initialTab: SubscriberListType
)