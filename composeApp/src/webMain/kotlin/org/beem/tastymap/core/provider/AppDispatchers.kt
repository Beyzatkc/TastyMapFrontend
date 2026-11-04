package org.beem.tastymap.core.provider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val AppDispatchers: DispatcherProvider = object : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.Default
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
}