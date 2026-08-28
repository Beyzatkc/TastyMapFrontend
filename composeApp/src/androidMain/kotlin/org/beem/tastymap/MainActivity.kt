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
        val dataUri = intent?.data
        Log.d("DEEPLINK_DEBUG", "Gelen Raw Intent Data: $dataUri")

        dataUri?.toString()?.let { url ->
            Log.d("DEEPLINK_DEBUG", "Yönlendirilen URL: $url")
            DeepLinkManager.handleLink(url)
        }
    }
}