package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beem.tastymap.data.model.Restaurant

class TastyMapState {
    var controller: MapController? by mutableStateOf(null)

    var selectedRestaurant by mutableStateOf<Restaurant?>(null)
        private set

    fun centerOn(lat: Double, lng: Double, zoom: Float = 15f){
        controller?.animateTo(lat, lng, zoom)
    }
    fun userMarker(lat: Double, lng: Double, title: String, bearing: Float){
        controller?.userMarker(lat, lng, title, bearing)
    }
    fun updateMapData(geoJson: String){
        controller?.updateMapData(geoJson)
    }

    fun setupMarkerClickListener() {
        controller?.setupRestaurantMarkerClickListener { restaurant ->
            selectedRestaurant = restaurant
        }
    }

    fun selectRestaurant(restaurant: Restaurant?) {
        selectedRestaurant = restaurant
    }
}

@Composable
fun rememberTastyMapState(): TastyMapState{
    return remember { TastyMapState() }
}