package org.beem.tastymap.ui.map.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SearchThisAreaChip(
    visible: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)