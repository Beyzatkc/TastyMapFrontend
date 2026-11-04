package org.beem.tastymap

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.beem.tastymap.data.repository.UserDeviceRepository
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IOSNotificationHelper : KoinComponent {
    // Sözdizimi hatası düzeltildi (by inject() eklendi)
    private val badgeManager: NotificationBadgeManager by inject()
    private val userDeviceRepository: UserDeviceRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun updateBadge(hasUnread: Boolean) {
        badgeManager.updateBadge(hasUnread)
    }

    fun updateFcmToken(token: String) {
        scope.launch {
            userDeviceRepository.updateFcmToken(fcmToken = token)
        }
    }
}