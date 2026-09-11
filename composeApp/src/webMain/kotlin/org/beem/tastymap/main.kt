package org.beem.tastymap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.cash.sqldelight.db.SqlDriver // EKLENDİ
import kotlinx.browser.window
import kotlinx.coroutines.MainScope // EKLENDİ
import kotlinx.coroutines.launch // EKLENDİ
import org.beem.tastymap.core.di.appModule
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.notification.setupWebNotificationBridge
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.di.webModule
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val koinApp = startKoin {
        modules(appModule, webModule)
    }

    MainScope().launch {
        val driver = koinApp.koin.get<SqlDriver>()

        TastyDatabase.Schema.create(driver).await()
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
}