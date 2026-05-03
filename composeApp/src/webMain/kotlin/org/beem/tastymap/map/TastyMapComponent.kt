package org.beem.tastymap.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import org.beem.tastymap.data.model.LocationData
import org.w3c.dom.HTMLDivElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
) {
    var provider by remember { mutableStateOf<MapProvider?>(null) }
    val mapDiv = remember { document.getElementById("map-container") as? HTMLDivElement }

    LaunchedEffect(mapUrl) {
        if (provider == null && mapDiv != null) {
            // Sadece haritayı başlat, div ile oynama
            provider = MapProvider(
                container = mapDiv,
                url = mapUrl,
                lat = userLocation.latitude,
                lng = userLocation.longitude
            )
        }
    }

    // UI tarafında hiçbir şey kaplamasın, sadece mantık çalışsın
    Spacer(modifier = Modifier.size(0.dp))
}