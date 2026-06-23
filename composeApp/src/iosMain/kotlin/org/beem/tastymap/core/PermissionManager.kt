package org.beem.tastymap.core

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import org.beem.tastymap.core.permission.PermissionManager

class IosPermissionManager(
    private val controller: PermissionsController
) : PermissionManager {
    override suspend fun requestNotificationPermission(): Boolean {
        return try {
            val state = controller.getPermissionState(Permission.REMOTE_NOTIFICATION)
            if (state == PermissionState.GRANTED) return true

            controller.providePermission(Permission.REMOTE_NOTIFICATION)

            controller.getPermissionState(Permission.REMOTE_NOTIFICATION) == PermissionState.GRANTED
        } catch (e: Exception) {
            false
        }
    }
}