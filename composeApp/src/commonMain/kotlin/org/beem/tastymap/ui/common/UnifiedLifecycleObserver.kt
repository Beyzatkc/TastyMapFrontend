package org.beem.tastymap.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun UnifiedLifecycleObserver(
    onActive: () -> Unit,
    onInactive: () -> Unit
)