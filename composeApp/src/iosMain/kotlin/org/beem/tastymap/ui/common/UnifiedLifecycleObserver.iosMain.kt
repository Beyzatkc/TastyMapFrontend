package org.beem.tastymap.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification

@Composable
actual fun UnifiedLifecycleObserver(
    onActive: () -> Unit,
    onInactive: () -> Unit
) {
    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        // Uygulama ön plana geldiğinde / aktifleştiğinde
        val activeObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onActive()
        }

        // Uygulama arka plana geçtiğinde / pasifleştiğinde
        val inactiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onInactive()
        }

        onDispose {
            notificationCenter.removeObserver(activeObserver)
            notificationCenter.removeObserver(inactiveObserver)
        }
    }
}