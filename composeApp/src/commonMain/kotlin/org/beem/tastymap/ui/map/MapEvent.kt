package org.beem.tastymap.ui.map

sealed interface MapEvent {
    data class AnimateCamera(val lat: Double, val lng: Double, val zoom: Float? = null) : MapEvent
    data class JumpCamera(val lat: Double, val lng: Double, val zoom: Float) : MapEvent
}