package org.beem.tastymap.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.ui.components.WebFabButton
import org.w3c.dom.HTMLElement

@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState,
    onFabClicked: () -> Unit
) {
    val mapId = "tastymap-actual-container"
    var isMapInitialized by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(mapId, mapUrl) {
        val existingDiv = document.getElementById(mapId)

        if (existingDiv == null) {
            val mapDiv = document.createElement("div") as HTMLElement
            mapDiv.id = mapId
            mapDiv.setAttribute("style", """
                position: absolute;
                top: 0; left: 0;
                width: 100%; height: 100%;
                z-index: 0;
                pointer-events: auto;
            """.trimIndent())

            document.body?.appendChild(mapDiv)

            val controller = WebMapController()
            state.controller = controller


            val fabBtn = WebFabButton.create(
                onClick = onFabClicked
            )
            document.body?.appendChild(fabBtn)

            coroutineScope.launch {
                controller.init(mapId, mapUrl)
                    .onSuccess {
                        isMapInitialized = true
                        state.setupMarkerClickListener()
                        println("Atlas: Web harita bileşeni başarıyla başlatıldı.")
                    }
                    .onFailure { error ->
                        println("Atlas Hatası: Harita başlatılamadı: ${error.message}")
                    }
            }
        } else {
            isMapInitialized = true
        }

        onDispose {
            val element = document.getElementById(mapId)
            element?.parentNode?.removeChild(element)
            isMapInitialized = false
        }
    }

    LaunchedEffect(userLocation, isMapInitialized) {
        if (isMapInitialized && userLocation.latitude != 0.0 && userLocation.longitude != 0.0) {
            state.controller?.animateTo(
                lat = userLocation.latitude,
                lng = userLocation.longitude,
                zoom = 15.0f
            )
        }
    }

    Box(modifier = modifier)
}