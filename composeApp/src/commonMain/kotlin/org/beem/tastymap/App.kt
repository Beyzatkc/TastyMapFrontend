package org.beem.tastymap

import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.beem.tastymap.core.auth.AuthEventBus
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.splash.SplashScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.theme.TastyTheme
import org.koin.compose.koinInject

@Composable
@Preview

fun App() {
    var isDark by remember { mutableStateOf(false) }
    val authEventBus: AuthEventBus = koinInject()

    TastyTheme(useDarkTheme = isDark) {
        Navigator(SplashScreen()) { navigator ->
            LaunchedEffect(Unit) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem !is SplashScreen) {
                        navigator.replaceAll(screen)
                    }
                }
            }
            LaunchedEffect(Unit) {
                authEventBus.events.collect { event ->
                    when (event) {
                        AuthEventBus.AuthEvent.OnUnauthenticated -> {
                            ToastManager.show("Oturum süreniz doldu, lütfen tekrar giriş yapın.")
                            navigator.replaceAll(LogRegScreen())
                        }
                    }
                }
            }
            SlideTransition(
                navigator = navigator,
                animationSpec = tween(400)
            )
        }
        AppToast()
    }

}


