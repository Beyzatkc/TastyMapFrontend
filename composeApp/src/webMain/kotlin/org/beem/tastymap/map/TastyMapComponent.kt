package org.beem.tastymap.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import org.beem.tastymap.data.model.LocationData
import androidx.compose.ui.viewinterop.WebElementView
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.w3c.dom.HTMLElement



@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
) {
    val mapId = "tastymap-actual-container"
    var isMapInitialized by remember { mutableStateOf(false) }

    SideEffect {
        if (kotlinx.browser.document.getElementById(mapId) == null) {
            val mapDiv = kotlinx.browser.document.createElement("div") as HTMLElement
            mapDiv.id = mapId
            mapDiv.setAttribute("style", """
                position: absolute;
                top: 0; left: 0;
                width: 100vw; height: 100vh;
                z-index: 100;
            """.trimIndent())
            kotlinx.browser.document.body?.appendChild(mapDiv)

            val controller = WebMapController()
            controller.init(mapId, mapUrl)
            state.controller = controller


        }
    }

    LaunchedEffect(userLocation, isMapInitialized) {
        if (isMapInitialized) {
            TastyMapBridge.flyTo(
                userLocation.latitude.toDouble(),
                userLocation.longitude.toDouble(),
                15.0
            )
        }
    }

    Box(modifier = modifier)
}