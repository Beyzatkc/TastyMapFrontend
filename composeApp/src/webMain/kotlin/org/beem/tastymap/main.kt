package org.beem.tastymap

import ProfileGraphRoute
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
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.notification.setupWebNotificationBridge
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.di.webModule
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.beem.tastymap.ui.profile.MyProfileFullScreenRoute
import org.beem.tastymap.ui.profile.ProfileRoute
import org.beem.tastymap.ui.splash.SplashRoute
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
        val userManager = koinApp.koin.get<UserManager>()
        val badgeManager = koinApp.koin.get<NotificationBadgeManager>()
        setupWebNotificationBridge(badgeManager)
        val currentUrl = window.location.href
        val myUsername = userManager.userSession.value?.username
        DeepLinkManager.handleLink(currentUrl)
        DeepLinkManager.handleWebRefresh(currentUrl,myUsername)

        ComposeViewport {
            App(
                initialRoute = SplashRoute,
                onNavHostReady = { navController ->
                    navController.bindToBrowserNavigation { entry ->
                        val destination = entry.destination

                        when {
                            // Splash ekranında URL tertemiz kalsın
                            destination.hasRoute<SplashRoute>() -> ""

                            // 1. BAŞKASININ PROFİLİ: Doğrudan kullanıcı adını göster (#kullaniciadi)
                            destination.hasRoute<ProfileRoute>() -> {
                                val profile = entry.toRoute<ProfileRoute>()
                                if (!profile.username.isNullOrBlank()) {
                                    "#${profile.username}" // İstersen "@" işaretini kaldırıp "#${profile.username}" yapabilirsin
                                } else {
                                    "#profile/${profile.userId}"
                                }
                            }

                            // 2. KENDİ PROFİLİM (Alt Bar veya Tam Ekran): Kendi kullanıcı adını göster!
                            destination.hasRoute<ProfileGraphRoute>() ||
                                    destination.hasRoute<MyProfileFullScreenRoute>() -> {
                                "#${userManager.userSession.value?.username ?: "Kullanıcı"}"
                            }

                            // 3. DİĞER TÜM EKRANLAR (#map, #search, #ai, #settings vb.)
                            else -> {
                                val route = destination.route.orEmpty()
                                val baseRouteName = route
                                    .substringBefore("/")
                                    .substringBefore("?")
                                val cleanName = baseRouteName.substringAfterLast(".")

                                if (cleanName.isNotBlank()) "#$cleanName" else ""
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