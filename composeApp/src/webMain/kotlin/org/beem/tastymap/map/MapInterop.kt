package org.beem.tastymap.map

@JsModule("./map_provider.js")
external class MapProvider(
    container: org.w3c.dom.HTMLElement,
    url: String,
    lat: Double,
    lng: Double
) {
    fun updateUserLocation(lat: Double, lng: Double)
    fun resize()
    fun destroy()
}