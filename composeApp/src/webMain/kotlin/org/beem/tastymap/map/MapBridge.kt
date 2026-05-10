package org.beem.tastymap.map

external object TastyMapBridge {
    fun initializeMap(containerId: String, mapUrl: String)
    fun flyTo(lat: Double, lng: Double, zoom: Double)
    fun updateGeoJson(sourceId: String, data: String)
    fun onMarkerClick(layerId: String, callback: (String) -> Unit)
    fun updateUserMarker(lat: Double, lng: Double, bearing: Float)
}