package org.beem.tastymap.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun TastyPullToRefreshBox (
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)