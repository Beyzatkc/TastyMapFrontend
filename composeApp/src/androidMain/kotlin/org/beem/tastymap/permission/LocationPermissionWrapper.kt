package org.beem.tastymap.permission

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun LocationPermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
){
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { onPermissionGranted() }
            content()
        }
        else -> {
            LaunchedEffect(permissionState) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    }
}