package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberTastyMapState(): TastyMapState{
    return remember { TastyMapState() }
}

class TastyMapState {
    var controller: MapController? by mutableStateOf(null)

    fun centerOn(lat: Double, lng: Double){
        controller?.animateTo(lat, lng)
    }
    fun userMarker(lat: Double, lng: Double, title: String, bearing: Float){
        controller?.userMarker(lat, lng, title, bearing)
    }
}