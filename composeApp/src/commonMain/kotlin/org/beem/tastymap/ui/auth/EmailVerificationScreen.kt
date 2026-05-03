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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject

class EmailVerificationScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val navyIcons = Color(0xFF001970)
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<AuthScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }

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

                ResendSection(
                    navyIcons= navyIcons,
                    screenModel = screenModel,
                    onResendClick = {
                       screenModel.resendMail(email)
                    }
                )

                TastyButton(
                    text = "Giriş Ekranına Dön",
                    onClick = {
                        navigator.replace(LogRegScreen())
                    },
                    isPrimary = true,
                    backcolor = navyIcons,
                    textcolor = navyIcons,
                    strokecolor = navyIcons
                )

            }
        }
    }
}
@Composable
fun ResendSection(onResendClick: () -> Unit, navyIcons: Color, screenModel: AuthScreenModel,) {
    val timeLeft by screenModel.timeLeft.collectAsState()
    val isButtonEnabled = timeLeft == 0

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        TextButton(
            enabled = isButtonEnabled,
            onClick = {
                if (isButtonEnabled) {
                    screenModel.startTime()
                    onResendClick()
                }
            }
        ){
            Text(
                text = if (isButtonEnabled) "E-posta gelmedi mi? Tekrar gönder"
                else "Tekrar göndermek için bekleyin: ${timeLeft}s",
                color = if (isButtonEnabled) navyIcons else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
