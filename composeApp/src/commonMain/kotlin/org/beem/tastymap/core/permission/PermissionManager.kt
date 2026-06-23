package org.beem.tastymap.core.permission

//WEBDE YOK IOS VE ANDROIDDE VAR
interface PermissionManager {
    suspend fun requestNotificationPermission(): Boolean
}