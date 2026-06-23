package org.beem.tastymap.core.permisson

import org.beem.tastymap.core.permission.PermissionManager

class WebPermissionManager : PermissionManager {
    override suspend fun requestNotificationPermission(): Boolean {
        println("Web üzerinde bildirim izni şu an desteklenmiyor.")
        return false
    }
}