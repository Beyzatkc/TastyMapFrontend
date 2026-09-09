package org.beem.tastymap.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.beem.tastymap.MainActivity
import org.beem.tastymap.data.repository.UserDeviceRepository
import org.beem.tastymap.ui.common.NotificationBadgeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val badgeManager: NotificationBadgeManager by inject()
    private val userDeviceRepository: UserDeviceRepository by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 1. Rozet durumunu güncelle
        badgeManager.updateBadge(true)

        // 2. Data payload'dan başlık ve içeriği al
        val title = message.data["title"]
        val body = message.data["body"]

        // 3. Bildirimi cihaz ekranında oluştur ve göster
        if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
            showNotification(title, body, message.data)
        }
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "tastymap_notifications"

        // Android 8.0 (API 26) ve üzeri için Bildirim Kanalı şarttır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "TastyMap Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirime tıklandığında MainActivity'e gidip extras verilerini iletecek Intent
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // Data içerisindeki type, userId gibi tüm verileri MainActivity'ye aktarıyoruz
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Uygulamanızın bildirim ikonu (Örn: R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            userDeviceRepository.updateFcmToken(fcmToken = token)
        }
    }
}