package org.beem.tastymap.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toAndroidColor(): Int = this.toArgb()