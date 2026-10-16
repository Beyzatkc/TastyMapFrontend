package org.beem.tastymap.ui.bottomnav

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.SocialNotificationsRepository
import org.beem.tastymap.ui.common.NotificationBadgeManager

class MainScreenModel(
    private val socialNotificationsRepository: SocialNotificationsRepository,
    private val badgeManager: NotificationBadgeManager
) : ScreenModel {

     fun checkUnreadNotifications() {
        if (badgeManager.hasUnreadBadge.value) {
            return
        }

        screenModelScope.launch {
            when (val result = socialNotificationsRepository.checkHasUnread()) {
                is ResultWrapper.Success -> {
                    badgeManager.updateBadge(result.data)
                }
                is ResultWrapper.Error -> {
                    println("NOTIFICATION ERROR: " + result.message)
                }
            }
        }
    }
}