package org.beem.tastymap.core.permission

interface PermissionManager {
    suspend fun requestNotificationPermission(): Boolean
   // suspend fun checkNotificationPermission(): Boolean
}