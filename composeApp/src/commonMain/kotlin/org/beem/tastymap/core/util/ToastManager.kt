package org.beem.tastymap.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ToastManager {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _toastEvents = MutableStateFlow<ToastState.ToastEvent?>(null)
    val toastEvents = _toastEvents.asStateFlow()
    private var currentJob: Job? = null

    fun show(message: String, type: ToastState.ToastType = ToastState.ToastType.INFO) {
        currentJob?.cancel() 
        currentJob = scope.launch {
            val event = ToastState.ToastEvent(message, type)
            _toastEvents.value = event

            delay(3000)
            _toastEvents.value = null
        }
    }
}