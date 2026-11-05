package org.beem.tastymap.core.util

import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLElement
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.Promise
import kotlin.js.JsAny
import kotlin.js.js

/**
 * WasmJS platformu için JS Promise yapısını Kotlin Coroutine dünyasına bağlayan köprü.
 * Sınıf dışına yazılarak her yerden erişilebilir extension haline getirilmiştir.
 */
@OptIn(ExperimentalWasmJsInterop::class)
internal suspend fun <T : JsAny?> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then(
        onFulfilled = { value ->
            cont.resume(value)
            null
        },
        onRejected = { error ->
            cont.resumeWithException(RuntimeException(error.toString()))
            null
        }
    )
}



@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(element, transformValue) => { element.style.transform = transformValue; }")
external fun setElementTransform(element: JsAny, transformValue: String)

@OptIn(ExperimentalWasmJsInterop::class)
fun JsAny.setStyleTransform(transformValue: String) {
    setElementTransform(this, transformValue)
}


@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(element, cursorType) => { element.style.cursor = cursorType; }")
external fun setElementCursor(element: HTMLElement, cursorType: String)


fun HTMLElement.setStyleCursor(cursorType: String) {
    setElementCursor(this, cursorType)
}


@OptIn(ExperimentalWasmJsInterop::class)
fun executeDelayed(delayMillis: Int, callback: () -> Unit): Unit =
    js("setTimeout(callback, delayMillis)")

@OptIn(ExperimentalWasmJsInterop::class)
fun stringifyJsObject(obj: JsAny): String = js("JSON.stringify(obj)")

@OptIn(ExperimentalWasmJsInterop::class)
fun getJsArrayLength(array: JsAny): Int = js("array.length")

@OptIn(ExperimentalWasmJsInterop::class)
fun <T : JsAny> getJsArrayElement(array: JsAny, index: Int): T? = js("array[index]")