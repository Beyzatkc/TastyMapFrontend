package org.beem.tastymap.core.permission

import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

private fun requestNotificationPermissionJs(onResult: (Boolean) -> Unit) {
    js("""
        if (!("Notification" in window)) {
            onResult(false);
        } else {
            Notification.requestPermission()
                .then(permission => {
                    onResult(permission === "granted");
                })
                .catch(() => {
                    onResult(false);
                });
        }
    """)
}

class WebPermissionManager : PermissionManager {
    override suspend fun requestNotificationPermission(): Boolean {
        return try {
            suspendCoroutine { continuation ->
                requestNotificationPermissionJs { isGranted ->
                    continuation.resume(isGranted)
                }
            }
        } catch (e: Exception) {
            println("Web bildirim izni alınırken bir hata oluştu: ${e.message}")
            false
        }
    }
}