package org.beem.tastymap.core.navigation

import kotlinx.coroutines.flow.StateFlow

interface PlatformMessenger {
    fun post(message: String)
    fun listen(): StateFlow<String?>
    fun close()
}