package org.beem.tastymap

import ProfileGraphRoute
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.toRoute
import app.cash.sqldelight.db.SqlDriver
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
import my
import org.beem.tastymap.ui.post.EditPostRoute

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

        val startupHash = window.location.hash
        ComposeViewport {
            App(
                initialRoute = SplashRoute,
                onNavHostReady = { navController ->
                    if (startupHash.isNotEmpty() && window.location.hash == startupHash) {
                        window.history.replaceState(null, "", startupHash)
                    }

                    navController.bindToBrowserNavigation { entry ->
                        val destination = entry.destination

                        when {
                            destination.hasRoute<SplashRoute>() -> startupHash

                            destination.hasRoute<EditPostRoute>() -> {
                                val editRoute = entry.toRoute<EditPostRoute>()
                                "#editpost/${editRoute.postId}"
                            }

                            destination.hasRoute<ProfileRoute>() -> {
                                val profile = entry.toRoute<ProfileRoute>()
                                if (!profile.username.isNullOrBlank()) {
                                    "#${profile.username}"
                                } else {
                                    "#profile/${profile.userId}"
                                }
                            }

                            destination.hasRoute<ProfileGraphRoute>() ||
                                    destination.hasRoute<my>() ||
                                    destination.hasRoute<MyProfileFullScreenRoute>() -> {
                                "#${userManager.userSession.value?.username ?: "Kullanıcı"}"
                            }

                            else -> {
                                val route = destination.route.orEmpty()

                                val routeWithoutArgs = route.substringBefore("?")

                                val cleanName = if (routeWithoutArgs.contains(".")) {
                                    routeWithoutArgs.substringAfterLast(".")
                                } else {
                                    routeWithoutArgs
                                }

                                if (cleanName.isNotBlank()) "#$cleanName" else ""
                            }
                        }
                    }

                    window.addEventListener("popstate", {
                        val currentHash = window.location.hash.removePrefix("#")
                        val myUser = userManager.userSession.value?.username


                        if (navController.previousBackStackEntry != null) {
                            val currentDest = navController.currentDestination
                            val iscurrentlyOnCustomProfile = currentDest?.hasRoute<ProfileRoute>() == true ||
                                    currentDest?.hasRoute<my>() == true ||
                                    currentDest?.hasRoute<ProfileGraphRoute>() == true

                            if (iscurrentlyOnCustomProfile && !currentHash.equals(myUser, ignoreCase = true)) {
                                navController.popBackStack()
                            }
                        }
                    })
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