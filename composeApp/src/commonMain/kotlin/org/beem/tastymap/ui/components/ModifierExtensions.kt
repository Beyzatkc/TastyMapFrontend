package org.beem.tastymap.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.responsiveContentWidth(maxWidth: Dp = 800.dp): Modifier = this
    .widthIn(max = maxWidth)
    .fillMaxWidth()