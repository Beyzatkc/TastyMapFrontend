package org.beem.tastymap.ui.auth.verification
import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.constants.AuthConstants.MSG_VERIFICATION_FINISHED
import org.beem.tastymap.core.navigation.PlatformMessenger
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.koin.compose.koinInject

class EmailVerificationScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val colors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<EmailScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        val messenger = koinInject<PlatformMessenger>()
        val receivedMessage by messenger.listen().collectAsState()

        LaunchedEffect(receivedMessage) {
            if (receivedMessage == MSG_VERIFICATION_FINISHED) {
                navigator.replaceAll(LogRegScreen())
                messenger.close()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = colors.navy
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "E-postanı Doğrula",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.navy,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$email adresine bir doğrulama bağlantısı gönderdik. Lütfen e-posta kutunu kontrol et.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Bağlantının geçerlilik süresi 5 dakikadır.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                ResendSection(
                    navyIcons = colors.navy,
                    screenModel = screenModel,
                    onResendClick = { screenModel.resendMail(email) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TastyButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Giriş Ekranına Dön",
                    onClick = {
                        //animasyonlu yapılcak
                        if (navigator.canPop) {
                            navigator.pop()
                        } else {
                            //BURASI DEGICESEK ANA SYAFMI MI GIRIS KAYIT MI
                            navigator.replaceAll(LogRegScreen())
                        }
                    },
                    isPrimary = true,
                    backcolor = colors.navy,
                    textcolor = Color.White,
                    strokecolor = colors.navy,
                )
            }
        }
    }
}
@Composable
fun ResendSection(onResendClick: () -> Unit, navyIcons: Color, screenModel: EmailScreenModel,) {
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
                text = if (isButtonEnabled) {
                    "E-posta gelmedi mi? Tekrar gönder"
                } else {
                    "Tekrar gönder: ${timeLeft}s"
                },
                color = if (isButtonEnabled) navyIcons else Color.Gray,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
