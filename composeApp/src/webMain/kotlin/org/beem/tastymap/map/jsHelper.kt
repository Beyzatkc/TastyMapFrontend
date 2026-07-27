package org.beem.tastymap.map

import kotlin.js.JsAny
import kotlin.js.js


fun getMapInstance(): dynamic = js("window.TastyMapBridge ? window.TastyMapBridge.map : null")

fun setCanvasCursor(cursorType: String) {
    js("""
        if (window.TastyMapBridge && window.TastyMapBridge.map) {
            window.TastyMapBridge.map.getCanvas().style.cursor = cursorType;
        }
    """)
}

fun stringifyJsObject(obj: JsAny): String = js("JSON.stringify(obj)")

fun getJsArrayLength(array: JsAny): Int = js("array.length")

fun getJsArrayElement(array: JsAny, index: Int): MapFeature? = js("array[index]")