package org.beem.tastymap

import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.ui.auth.LogRegScreen
import org.beem.tastymap.ui.auth.Splash.SplashScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.theme.TastyTheme

@Composable
@Preview

fun App() {
    var isDark by remember { mutableStateOf(false) }
    TastyTheme(useDarkTheme = isDark) {
        Navigator(SplashScreen()) { navigator ->
            LaunchedEffect(Unit) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem !is SplashScreen) {
                        navigator.replaceAll(screen)
                    }
                }
            }
            SlideTransition(
                navigator = navigator,
                animationSpec = tween(400)
            )
        }
/*
        Navigator(SplashScreen()) { navigator ->
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                DeepLinkManager.navigationEvents.collect { screen ->
                    if (navigator.lastItem::class != screen::class) {
                        navigator.replaceAll(screen)
                    }
                }
            }
                SlideTransition(
                    navigator = navigator,
                    animationSpec = tween(400)
                )
         }

 */
        AppToast()
    }

}


