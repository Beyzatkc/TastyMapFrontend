package org.beem.tastymap.ui.auth

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen

class EmailVerificationScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val navyIcons = Color(0xFF001970)

        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .imePadding(),
        )
        {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = navyIcons
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "E-postanı Doğrula",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = navyIcons
                )

                Text(
                    text = "$email adresine bir doğrulama bağlantısı gönderdik. Lütfen e-posta kutunu kontrol et.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Bağlantının geçerlilik süresi 5 dakikadır.",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TastyButton(
                    text = "Giriş Ekranına Dön",
                    onClick = { /* Navigator.popUntilRoot() veya Login'e yönlendir */ },
                    isPrimary = true,
                    backcolor = navyIcons,
                    textcolor = navyIcons,
                    strokecolor = navyIcons
                )

                TextButton(onClick = { /* Yeniden gönderim logic'i */ }) {
                    Text("E-posta gelmedi mi? Tekrar gönder", color = navyIcons)
                }
            }
        }
    }
}