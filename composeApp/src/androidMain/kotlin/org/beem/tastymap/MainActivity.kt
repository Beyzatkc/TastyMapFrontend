package org.beem.tastymap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp
import org.beem.tastymap.core.navigation.DeepLinkManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        Log.d("DEEPLINK", "ONCREATE INTENT: ${intent?.data}")

        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        intent?.data?.toString()?.let {
            Log.d("DEEPLINK", "HANDLE LINK CALLED: $it")
            DeepLinkManager.handleLink(it)
        }

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.toString()?.let {
            Log.d("DEEPLINK", "NEW INTENT: ${intent.data}")
            DeepLinkManager.handleLink(it)
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
