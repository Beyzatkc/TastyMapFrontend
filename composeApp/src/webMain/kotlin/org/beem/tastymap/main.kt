package org.beem.tastymap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.notification.setupWebNotificationBridge
import org.beem.tastymap.di.webModule
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import kotlin.getValue

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val koinApp = startKoin {
        modules(appModule, webModule)
    }
    val badgeManager = koinApp.koin.get<NotificationBadgeManager>()

    setupWebNotificationBridge(badgeManager)
    DeepLinkManager.handleLink(window.location.href)

    window.onpopstate = {
        DeepLinkManager.handleLink(window.location.href)
    }

    ComposeViewport {
        App()
    }
}