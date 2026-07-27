package org.beem.tastymap.map

import kotlin.js.JsAny

external interface MapMouseEvent : JsAny {
    val features: JsArray<MapFeature>?
}

external interface MapFeature : JsAny {
    val properties: JsAny?
}

external interface JsArray<T : JsAny> : JsAny {
    val length: Int
    operator fun get(index: Int): T?
}