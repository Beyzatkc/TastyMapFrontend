package org.beem.tastymap.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification

@Composable
actual fun UnifiedLifecycleObserver(
    onActive: () -> Unit,
    onInactive: () -> Unit
) {
    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        val activeObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = null,
            usingBlock = { _ -> onActive() }
        )

        val inactiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = null,
            usingBlock = { _ -> onInactive() }
        )

        onActive()

        onDispose {
            notificationCenter.removeObserver(activeObserver)
            notificationCenter.removeObserver(inactiveObserver)
            onInactive()
        }
    }
}