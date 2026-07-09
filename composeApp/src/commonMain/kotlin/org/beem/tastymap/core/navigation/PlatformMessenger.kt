package org.beem.tastymap.core.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PlatformMessenger {
    fun post(message: String)
    fun listen(): Flow<String>
    fun close()
}