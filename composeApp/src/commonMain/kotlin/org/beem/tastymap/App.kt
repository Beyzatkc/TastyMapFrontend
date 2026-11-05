package org.beem.tastymap

import MainRoute
import MapGraphRoute
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
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
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.domain.auth.ClearSessionUseCase
import org.beem.tastymap.ui.auth.LogRegRoute
import org.beem.tastymap.ui.auth.ResetRoute
import org.beem.tastymap.ui.auth.VerifyRoute
import org.beem.tastymap.ui.auth.authGraph
import org.beem.tastymap.ui.bottomnav.MainAppShell
import org.beem.tastymap.ui.bottomnav.mainGraph
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.post.postGraph
import org.beem.tastymap.ui.profile.OnBoardingRoute
import org.beem.tastymap.ui.profile.profileGraph
import org.beem.tastymap.ui.splash.SplashEffect
import org.beem.tastymap.ui.splash.SplashLogoView
import org.beem.tastymap.ui.splash.SplashViewModel
import org.beem.tastymap.ui.splash.splashGraph
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.auth_logged_out
import tastymap.composeapp.generated.resources.auth_password_changed
import tastymap.composeapp.generated.resources.auth_session_expired

val LocalIsWideScreen = compositionLocalOf { false }
@Composable
@Preview
fun App(
    initialRoute: Any? = null,
    onNavHostReady: suspend (androidx.navigation.NavController) -> Unit = {}
) {
    val splashViewModel = koinViewModel<SplashViewModel>()
    val splashEffect by splashViewModel.effect.collectAsState()

    val settingsManager: SettingsManager = koinInject()
    val isDarkModePref by settingsManager.isDarkMode.collectAsState()
    val languageCode by settingsManager.languageCode.collectAsState()

    val authEventBus: AuthEventBus = koinInject()
    val clearSessionUseCase: ClearSessionUseCase = koinInject()

    val useDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

    ChangeAppLanguage(languageCode)

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
        if (splashEffect == null) {
            // NAVHOST HENÜZ OLUŞTURULMADI. URL TİTREMEZ!
            SplashLogoView() // Ekranda sadece logo göster
        } else {
            // 1. NAVCONTROLLER BURAYA TAŞINDI!
            // Artık NavHost'un çizileceğinden eminiz, NavController'ı güvenle yaratabiliriz.
            val navController = rememberNavController()

            // 2. DİNLEYİCİLER BURAYA TAŞINDI!
            LaunchedEffect(navController) {
                DeepLinkManager.navigationEvents.collect { route ->
                    navController.currentBackStackEntry?.destination?.route?.let { currentRoute ->
                        if (!currentRoute.contains("Splash")) {
                            navController.navigate(route) {
                                /*
                                val popTarget: Any = if (route is VerifyRoute || route is ResetRoute) LogRegRoute else MainRoute
                                popUpTo(popTarget) { inclusive = false }

                                 */
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            LaunchedEffect(navController) {
                authEventBus.events.collect { event ->
                    println("AUTH_EVENT_BUS TETİKLENDİ: $event")
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

            val safeStartDest = remember(splashEffect) {
                when (val effect = splashEffect) {

                    is SplashEffect.NavigateToDeepLink -> {
                        when (effect.route) {
                            is VerifyRoute, is ResetRoute -> LogRegRoute
                            else -> MainRoute
                        }
                    }


                    is SplashEffect.NavigateToHome -> MainRoute
                    is SplashEffect.NavigateToLogin -> LogRegRoute
                    is SplashEffect.NavigateToOnBoard -> OnBoardingRoute
                    else -> LogRegRoute
                }
            }
            val jumpRoute = remember(splashEffect) {
                val route = (splashEffect as? SplashEffect.NavigateToDeepLink)?.route
                if (route == MainRoute || route == MapGraphRoute) null else route
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth >= 600.dp
                CompositionLocalProvider(LocalIsWideScreen provides isWideScreen) {

                    MainAppShell(navController = navController) {
                        NavHost(
                            navController = navController,
                            startDestination = safeStartDest,
                            modifier = Modifier.fillMaxSize().background(LocalCustomColors.current.surface),
                            //modifier = Modifier.fillMaxSize(),
                            enterTransition = { fadeIn(animationSpec = tween(400)) },
                            exitTransition = { fadeOut(animationSpec = tween(400)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                            popExitTransition = { fadeOut(animationSpec = tween(400)) }
                        ) {
                            splashGraph(navController)
                            authGraph(navController)
                            mainGraph(navController)
                            postGraph(navController)
                            profileGraph(navController)
                        }
                    }
                    LaunchedEffect(navController) {
                        // 1. Önce jumpRoute varsa doğrudan hedefe git
                        if (jumpRoute != null) {
                            navController.navigate(jumpRoute) {
                                // Eğer bir ana sekmeye (Profile, Search, AI, Upload) F5 ile gelindiyse,
                                // arkada fazladan "map" kalıp geçmişi şişirmemesi için MainRoute'a kadar temizle:
                                popUpTo(safeStartDest) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                        // 2. Navigasyon yerleştikten SONRA tarayıcı geçmişini bağla
                        onNavHostReady(navController)
                    }


                    /*

                    LaunchedEffect(navController, jumpRoute) {
                        if (jumpRoute != null) {
                            navController.navigate(jumpRoute) {
                                popUpTo(safeStartDest) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        onNavHostReady(navController)
                    }

                     */

                    AppToast()
                }
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
