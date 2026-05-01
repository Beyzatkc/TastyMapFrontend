package org.beem.tastymap.core.util

sealed class ToastState {
    enum class ToastType { SUCCESS, ERROR, INFO }

    data class ToastEvent(
        val message: String,
        val type: ToastType = ToastType.INFO,
        val duration: Long = 1500L
    )
}