package org.beem.tastymap.core.util

import kotlin.time.Clock

actual fun currentEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()