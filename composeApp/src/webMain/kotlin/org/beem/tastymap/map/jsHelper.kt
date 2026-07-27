package org.beem.tastymap.map

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js


fun getMapInstance(): JsAny? = js("window.TastyMapBridge ? window.TastyMapBridge.map : null")

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(cursorType) => window.TastyMapBridge.setCanvasCursor(cursorType)")
external fun setCanvasCursor(cursorType: String)

fun getJsArrayLength(array: JsAny): Int = js("array.length")

fun getJsArrayElement(array: JsAny, index: Int): MapFeature? = js("array[index]")

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(layerId, callback) => window.TastyMapBridge.addLayerClickListener(layerId, callback)")
external fun addLayerClickListener(layerId: String, callback: (String) -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(layerId) => window.TastyMapBridge.addLayerHoverListener(layerId)")
external fun addLayerHoverListener(layerId: String)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(obj) => JSON.stringify(obj)")
external fun stringifyJsObject(obj: JsAny): String