package org.beem.tastymap.core

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import org.beem.tastymap.core.permission.PermissionManager

class AndroidPermissionManager(
    private val context: Context,
    private val controller: PermissionsController
) : PermissionManager {
    override suspend fun requestNotificationPermission(): Boolean {
        return try {

            val state = controller.getPermissionState(Permission.REMOTE_NOTIFICATION)
            if (state == PermissionState.Granted) return true

            controller.providePermission(Permission.REMOTE_NOTIFICATION)

            controller.getPermissionState(Permission.REMOTE_NOTIFICATION) == PermissionState.Granted
        } catch (e: Exception) {
            Log.d("Hata bıldırkm", e.toString())
            false
        }
    }

    override suspend fun checkNotificationPermission(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }


}