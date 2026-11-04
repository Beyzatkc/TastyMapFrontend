package org.beem.tastymap.ui.bottomnav
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.SocialNotificationsRepository
import org.beem.tastymap.ui.common.NotificationBadgeManager

class MainViewModel(
    private val socialNotificationsRepository: SocialNotificationsRepository,
    private val badgeManager: NotificationBadgeManager
) : ViewModel() {

     fun checkUnreadNotifications() {
        if (badgeManager.hasUnreadBadge.value) {
            return
        }

        viewModelScope.launch {
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