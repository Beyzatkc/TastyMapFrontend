package org.beem.tastymap.ui.tastyview

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTastyScrollState = compositionLocalOf { mutableStateOf(0) } //staticCompositionLocalOf<ScrollState?> { null }

internal val currentTriggerPixelOffset = mutableStateOf(0)

val LocalIsPagingActive = staticCompositionLocalOf { false }