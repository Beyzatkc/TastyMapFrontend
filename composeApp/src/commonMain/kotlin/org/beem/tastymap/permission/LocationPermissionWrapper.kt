package org.beem.tastymap.permission

import androidx.compose.runtime.Composable

@Composable
expect fun LocationPermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
)