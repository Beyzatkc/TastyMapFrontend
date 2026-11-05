package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
expect fun TastyMapFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: String = "#FF5722",
    iconColor: String = "#FFFFFF",
    zIndex: Float = 101f,
    icon: TastyIcon = TastyIcon.ADD
)