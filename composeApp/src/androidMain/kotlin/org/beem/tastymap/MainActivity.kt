package org.beem.tastymap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp
import dev.icerock.moko.permissions.PermissionsController
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {

    private val permissionsController by lazy {
        PermissionsController(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        permissionsController.bind(this)
        getKoin().declare(permissionsController)
        FirebaseApp.initializeApp(this)

        // 1. Uygulama Kapalıyken Gelen Linki İşle
        processDeepLink(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 2. Uygulama Açık/Arkaplandayken Gelen Linki İşle
        processDeepLink(intent)
    }

    private fun processDeepLink(intent: Intent?) {
        // 1. Önce normal web linki (URI) ile mi gelindi ona bakalım
        val dataUri = intent?.data
        if (dataUri != null) {
            Log.d("DEEPLINK_DEBUG", "Gelen Raw Intent Data: $dataUri")
            DeepLinkManager.handleLink(dataUri.toString())
            return // İşlem bitti, çık
        }

        // 2. FCM Bildirimine tıklanarak mı gelindi? (Extras kontrolü)
        val extras = intent?.extras
        if (extras != null) {
            val type = extras.getString("type")
            val userId = extras.getString("userId") // Takip bildirimi için backend'den yolladığımız id

            Log.d("FCM_DEBUG", "Bildirime Tıklandı - Type: $type, UserId: $userId")

            when (type) {
                "FOLLOW_REQUEST", "NEW_FOLLOWER" -> {
                    if (userId != null) {
                        val profileLink = "https://coleman-nonethic-marinda.ngrok-free.dev/profile/$userId"
                        DeepLinkManager.handleLink(profileLink)
                    }
                }
            }
        }
    }
}