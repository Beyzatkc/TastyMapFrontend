package org.beem.tastymap.core.util

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise
import kotlin.js.JsAny

/**
 * WasmJS platformu için JS Promise yapısını Kotlin Coroutine dünyasına bağlayan köprü.
 * Sınıf dışına yazılarak her yerden erişilebilir extension haline getirilmiştir.
 */
internal suspend fun <T : JsAny?> Promise<T>.await(): T = suspendCoroutine { cont ->
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