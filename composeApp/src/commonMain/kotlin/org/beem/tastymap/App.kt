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

    TastyTheme(useDarkTheme = isDark) {
        //Navigator(LogRegScreen())

        Navigator(LogRegScreen()) { navigator ->
            LaunchedEffect(navigator) {
                DeepLinkManager.navigationEvents.collect { screen ->
                    val isAlreadyOnVerify = navigator.lastItem is VerifyScreen

                    if (isAlreadyOnVerify) {
                        navigator.replace(screen)
                    } else {
                        navigator.replaceAll(screen)
                    }
                }
            }
            CurrentScreen()
        }
        AppToast()


    }
}