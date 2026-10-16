package org.beem.tastymap.core.notification

import kotlinx.browser.window
import org.beem.tastymap.ui.common.NotificationBadgeManager

fun setupWebNotificationBridge(badgeManager: NotificationBadgeManager) {
    println("WEB_DEBUG: setupWebNotificationBridge fonksiyonu ÇAĞRILDI! Dinleyici ekleniyor...")

    window.addEventListener("onNotificationReceived", {
        println("WEB_DEBUG: onNotificationReceived EVENT'i Kotlin tarafında YAKALANDI! Rozet güncelleniyor...")
        badgeManager.updateBadge(true)
    })
}