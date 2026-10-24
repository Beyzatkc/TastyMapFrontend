package org.beem.tastymap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.toRoute
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
import org.beem.tastymap.ui.profile.ProfileRoute
import org.koin.core.context.startKoin
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalBrowserHistoryApi::class
)
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
            App(
                onNavHostReady = { navController ->

                    navController.bindToBrowserNavigation { entry ->
                        val destination = entry.destination

                        when {
                            // 1. İSİMLERİ (USERNAME) GÖSTERMEK İSTEDİĞİMİZ ROTALAR
                            destination.hasRoute<ProfileRoute>() -> {
                                val profile = entry.toRoute<ProfileRoute>()
                                "#profile/${profile.username}" // URL'de profil ismini tutar
                            }

                            // 2. ID'LERİ GİZLEMEK İSTEDİĞİNİZ DİĞER ROTALAR (Otomatik Kural)
                            else -> {
                                val route = destination.route.orEmpty()

                                // Önce "/" işaretinden sonrasını kes (ID'leri ve parametreleri at)
                                val baseRouteName = route.substringBefore("/")

                                // Sonra "org.beem.tastymap.ui.HomeRoute" gibi paket adlarını temizle,
                                // sadece "HomeRoute" (veya ne isim verdiyseniz) kalsın
                                val cleanName = if (baseRouteName.contains(".")) {
                                    baseRouteName.substringAfterLast(".")
                                } else {
                                    baseRouteName
                                }

                                if (cleanName.isNotBlank()) {
                                    "#$cleanName"
                                } else {
                                    ""
                                }
                            }
                        }
                    }

                }
            )
        }
/*
        ComposeViewport {
            App()
        }

 */
    }
}