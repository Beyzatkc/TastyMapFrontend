package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTastyScrollState = staticCompositionLocalOf<ScrollState?> { null }

internal val currentTriggerPixelOffset = mutableStateOf(0)