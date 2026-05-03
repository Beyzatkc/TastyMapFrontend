package org.beem.tastymap.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

// Bu fonksiyon Compose'u aşar ve doğrudan tarayıcının JavaScript motoruna emir verir.
// Kotlin derleyicisi bu string'in içine karışmadığı için "kırmızı yanma" sorunu biter.
private fun requestLocationFromBrowser(onGranted: () -> Unit) {
    js("""
        if (window.navigator && window.navigator.geolocation) {
            window.navigator.geolocation.getCurrentPosition(
                function(position) { 
                    onGranted(); 
                },
                function(error) { 
                    console.log("Tarayıcı konum izni verilmedi veya hata: ", error); 
                    // Kullanıcı izni reddetse bile harita siyah ekranda kalmasın diye
                    // akışı devam ettiriyoruz.
                    onGranted(); 
                }
            );
        } else {
            console.log("Bu tarayıcı konum servisini desteklemiyor.");
            onGranted();
        }
    """)
}

@Composable
actual fun LocationPermissionWrapper(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        requestLocationFromBrowser {
            onPermissionGranted()
        }
    }

    // Hiçbir arka plan, Surface veya Box yok! Tamamen şeffaf cam.
    content()
}