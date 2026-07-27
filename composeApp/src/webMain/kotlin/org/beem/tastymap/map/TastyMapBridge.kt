package org.beem.tastymap.map

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.Promise

@JsName("TastyMapBridge")
external object TastyMapBridge {
    val isMapLibreLoaded: Boolean

    @OptIn(ExperimentalWasmJsInterop::class)
    fun loadMapLibreSdk(): Promise<JsAny?>
    @OptIn(ExperimentalWasmJsInterop::class)
    fun initializeMap(containerId: String, mapUrl: String): Promise<JsAny?>
    fun flyTo(lat: Double, lng: Double, zoom: Double)
    fun updateGeoJson(sourceId: String, data: String)
    fun onMarkerClick(layerId: String, callback: (String) -> Unit)
    fun updateUserMarker(lat: Double, lng: Double, bearing: Float)
}