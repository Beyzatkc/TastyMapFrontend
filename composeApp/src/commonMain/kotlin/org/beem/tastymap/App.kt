package org.beem.tastymap

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.local.ChangeAppLanguage
import org.beem.tastymap.core.local.SettingsManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.database.TastyDatabase
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.authGraph
import org.beem.tastymap.ui.bottomnav.mainGraph
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.post.postGraph
import org.beem.tastymap.ui.profile.profileGraph
import org.beem.tastymap.ui.profile.subscribers.SubscriberListNavType
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
import org.beem.tastymap.ui.search.searchGraph
import org.beem.tastymap.ui.splash.SplashRoute
import org.beem.tastymap.ui.splash.SplashScreen
import org.beem.tastymap.ui.splash.splashGraph
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.account_deactivated_subtitle
import tastymap.composeapp.generated.resources.auth_logged_out
import tastymap.composeapp.generated.resources.auth_password_changed
import tastymap.composeapp.generated.resources.auth_session_expired
import kotlin.reflect.typeOf

// App.kt dosyasının importlarının hemen altına ekle:
val LocalIsWideScreen = compositionLocalOf { false }
@Composable
@Preview
fun App(onNavHostReady: suspend (androidx.navigation.NavController) -> Unit = {}) {
    val settingsManager: SettingsManager = koinInject()
    val isDarkModePref by settingsManager.isDarkMode.collectAsState()
    val languageCode by settingsManager.languageCode.collectAsState()

    val authEventBus: AuthEventBus = koinInject()
    val clearSessionUseCase: ClearSessionUseCase = koinInject()

    val useDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

    ChangeAppLanguage(languageCode)

    val navController = rememberNavController()
    TastyTheme(useDarkTheme = useDarkTheme) {

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components {
                    add(
                        KtorNetworkFetcherFactory(
                            httpClient = HttpClient {
                                install(DefaultRequest) {
                                    header("ngrok-skip-browser-warning", "true")
                                }
                            }
                        )
                    )
                }
                .build()
        }
        LaunchedEffect(navController) {
            DeepLinkManager.navigationEvents.collect { route ->
                navController.currentBackStackEntry?.destination?.route?.let { currentRoute ->
                    if (!currentRoute.contains("Splash")) {
                        navController.navigate(route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
        LaunchedEffect(navController) {
            authEventBus.events.collect { event ->
                clearSessionUseCase()

                val message = when (event) {
                    is AuthEventBus.AuthEvent.OnSessionExpired -> getString(Res.string.auth_session_expired)
                    is AuthEventBus.AuthEvent.OnPasswordChanged -> getString(Res.string.auth_password_changed)
                    is AuthEventBus.AuthEvent.OnLoggedOut -> getString(Res.string.auth_logged_out)
                    is AuthEventBus.AuthEvent.DeleteAccount -> null
                }

                message?.let {
                    ToastManager.show(it)
                }

                navController.navigate(LogRegRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp // Geniş ekran kontrolü

            // Tüm uygulamaya isWideScreen bilgisini en tepeden veriyoruz!
            CompositionLocalProvider(
                LocalIsWideScreen provides isWideScreen
            ) {
                NavHost(
                    navController = navController,
                    startDestination = SplashRoute,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                    popExitTransition = { fadeOut(animationSpec = tween(400)) }
                ) {
                    authGraph(navController)
                    mainGraph(navController)
                    postGraph(navController)
                    profileGraph(navController)
                    searchGraph(navController)
                    splashGraph(navController)

                }
                LaunchedEffect(navController) {
                    onNavHostReady(navController)
                }

                AppToast()
            }
        }
    }
}


/*
        Navigator(SplashScreen()) { navigator ->

            // DeepLink dinleyicisi
            LaunchedEffect(navigator) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem !is SplashScreen) {
                        navigator.replaceAll(screen)
                    }
                }
            }

            // Oturum durum dinleyicisi
            LaunchedEffect(navigator) {
                authEventBus.events.collect { event ->
                    clearSessionUseCase()

                    val message = when (event) {
                        is AuthEventBus.AuthEvent.OnSessionExpired -> getString(Res.string.auth_session_expired)
                        is AuthEventBus.AuthEvent.OnPasswordChanged -> getString(Res.string.auth_password_changed)
                        is AuthEventBus.AuthEvent.OnLoggedOut -> getString(Res.string.auth_logged_out)
                        is AuthEventBus.AuthEvent.DeleteAccount -> null
                    }

                    message?.let {
                        ToastManager.show(it)
                    }
                    navigator.replaceAll(LogRegScreen())
                }
            }

            SlideTransition(
                navigator = navigator,
                animationSpec = tween(400)
            )
        }
        AppToast()
    }

 */
