package org.beem.tastymap

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.ui.auth.LogRegScreen
import org.beem.tastymap.ui.auth.VerifyScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.theme.TastyTheme


@Composable
@Preview
fun App() {
    var isDark by remember { mutableStateOf(false) }
    var startScreen by remember { mutableStateOf<cafe.adriel.voyager.core.screen.Screen?>(null) }

    LaunchedEffect(Unit) {
        startScreen = DeepLinkManager.pendingInitialScreen ?: LogRegScreen()
        DeepLinkManager.pendingInitialScreen = null
    }

    TastyTheme(useDarkTheme = isDark) {
        startScreen?.let { initial ->
            Navigator(initial) { navigator ->
                LaunchedEffect(navigator) {
                    DeepLinkManager.navigationEvents.collect { screen ->
                        if (navigator.lastItem::class != screen::class) {
                            navigator.replaceAll(screen)
                        }
                    }
                }
                CurrentScreen()
            }
        }
        AppToast()


    }
}