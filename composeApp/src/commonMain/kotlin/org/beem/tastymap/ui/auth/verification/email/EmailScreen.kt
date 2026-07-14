package org.beem.tastymap.ui.auth.verification.email

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.core.navigation.VerifyNavigator
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.logReg.LogRegScreen

import org.beem.tastymap.ui.common.UnifiedLifecycleObserver
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.koin.compose.koinInject

class EmailVerificationScreen(val email: String, val deviceId: String,val userId: Long) : Screen {
    @Composable
    override fun Content() {
        val colors = LocalCustomColors.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<EmailScreenModel>()
        val navigationEffect by screenModel.navigationState.collectAsState(initial = null)
        val verifyNavigator = koinInject<VerifyNavigator>()

        LaunchedEffect(navigationEffect) {
            when (navigationEffect) {
                EmailScreenModel.EmailNavEffect.OnSuccess -> {
                    verifyNavigator.verifyEmailNavigationTwo(navigator)
                }

                null -> Unit
            }
        }

        LaunchedEffect(Unit) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

        LaunchedEffect(userId, deviceId) {
            screenModel.setVerificationContext(
                userId,
                deviceId
            )
        }
        UnifiedLifecycleObserver(
            onActive = {
                println("LIFECYCLE: Ekran aktif (Resume/Focus/Visible). Model tetikleniyor.")
                screenModel.handleLifecycleEvent(AuthLifecycleEvent.Resume)
            },
            onInactive = {
                println("LIFECYCLE: Ekran pasif (Stop/Blur/Hidden). Model temizleniyor.")
                screenModel.handleLifecycleEvent(AuthLifecycleEvent.Stop)
            }
        )

        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                BackPage("Doğrulama", {
                    navigator.pop()
                })
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {

                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(
                                    color = colors.navy.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colors.navy
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "E-postanı Doğrula",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.navy,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$email adresine bir doğrulama bağlantısı gönderdik. Lütfen e-posta kutunu kontrol et.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        ResendSection(
                            navyIcons = colors.navy,
                            screenModel = screenModel,
                            onResendClick = { screenModel.resendMail(deviceId, email) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TastyButton(
                            text = "Giriş Ekranına Dön",
                            onClick = {
                                if (navigator.canPop) {
                                    navigator.pop()
                                } else {
                                    navigator.replaceAll(LogRegScreen())
                                }
                            },
                            backcolor = colors.navy,
                            textcolor = Color.White,
                            strokecolor = colors.navy
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colors.navy.copy(alpha = 0.05f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Güvenlik uyarısı:\n")
                                        }
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                                            append("Gönderilen doğrulama bağlantısının geçerlilik süresi 10 dakikadır.")
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "E-posta kutunuzda göremiyorsanız Spam klasörünü kontrol etmeyi unutmayın.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                    }
                }
                AuthFooter(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ResendSection(onResendClick: () -> Unit, navyIcons: Color, screenModel: EmailScreenModel) {
    val timeLeft by screenModel.timeLeft.collectAsState()
    val isButtonEnabled = timeLeft == 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            enabled = isButtonEnabled,
            onClick = {
                if (isButtonEnabled) {
                    screenModel.startTimer()
                    onResendClick()
                }
            }
        ) {
            Text(
                text = if (isButtonEnabled) {
                    "E-posta gelmedi mi? Tekrar gönder"
                } else {
                    "Tekrar gönder: ${timeLeft}s"
                },
                color = if (isButtonEnabled) navyIcons else Color.Gray,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}