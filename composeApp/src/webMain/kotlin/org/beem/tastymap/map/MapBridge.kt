package org.beem.tastymap.map

external fun initializeMapLibreJS(containerId: String)
external fun updateWebSource(sourceId: String, geoJson: String)
external fun flyToWeb(lat: Double, lng: Double, zoom: Float)
external fun listenMarkerClick(layerId: String, callback: (String, String, Double, Double) -> Unit)
