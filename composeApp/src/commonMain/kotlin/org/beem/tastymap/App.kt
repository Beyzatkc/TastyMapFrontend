package org.beem.tastymap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.beem.tastymap.ui.auth.LoginScreen
import org.beem.tastymap.ui.components.AppToast
import org.beem.tastymap.ui.icons.TastyMapIconsManager
import org.beem.tastymap.ui.map.TastyMapScreen
import org.beem.tastymap.ui.theme.TastyTheme

@Composable
@Preview
fun App() {
    var isDark by remember { mutableStateOf(false) }
    val devChooseScreen = "Map"

    TastyMapIconsManager.initialize()

    if(TastyMapIconsManager.isReady.value){
        TastyMapScreen().Content()
    }
}