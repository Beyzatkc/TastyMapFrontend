package org.beem.tastymap.ui.auth.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CountdownTimer(private val scope: CoroutineScope) {

    companion object {
        private const val COUNTDOWN_SECONDS = 60
    }
    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft

    private var timerJob: Job? = null

    fun startTime() {

        if (_timeLeft.value > 0) return

        timerJob?.cancel()

        timerJob = scope.launch {
            _timeLeft.value = COUNTDOWN_SECONDS

            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value--
            }
        }
    }

    fun stopTime() {
        timerJob?.cancel()
        _timeLeft.value = 0
    }
}