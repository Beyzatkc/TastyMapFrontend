package org.beem.tastymap

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.beem.tastymap.ui.tastyview.icons.TastyMapIconsManager
import org.beem.tastymap.ui.map.TastyMapScreen

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