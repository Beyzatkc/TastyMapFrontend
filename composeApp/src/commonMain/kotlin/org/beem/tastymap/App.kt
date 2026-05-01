package org.beem.tastymap

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.beem.tastymap.ui.auth.LoginScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.map.TastyMapScreen
import org.beem.tastymap.ui.theme.TastyTheme

@Composable
@Preview
fun App() {
    var isDark by remember { mutableStateOf(false) }
    val devChooseScreen = "Map"

    TastyTheme(useDarkTheme = isDark) {
        when(devChooseScreen){
            "Map" -> {
                Navigator(TastyMapScreen())
            }
            "Auth" -> {
                Navigator(LoginScreen())
            }
        }
        AppToast()


    }
}