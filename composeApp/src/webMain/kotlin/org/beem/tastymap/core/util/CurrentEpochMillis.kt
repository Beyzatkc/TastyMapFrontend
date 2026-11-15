package org.beem.tastymap.core.util

import kotlinx.datetime.Clock

actual fun currentEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()