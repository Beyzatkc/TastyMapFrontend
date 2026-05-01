package org.beem.tastymap.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun LocationPermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
){
    LaunchedEffect(Unit){
        onPermissionGranted
    }
    content()
}