package org.beem.tastymap.ui.auth.forgotPassword

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import io.ktor.client.plugins.auth.Auth
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.splash.SplashScreen
import org.beem.tastymap.ui.auth.verification.EmailVerificationScreen
import org.beem.tastymap.ui.auth.verification.PendingScreen
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.components.BackPage
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.koin.compose.koinInject

class ForgotScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<ForgotScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        val state by screenModel.sendState.collectAsState()
        val colors = LocalCustomColors.current

        // Effect Handling
        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToHome -> navigator.replaceAll(SplashScreen())
                    is AuthEffect.NavigateToLogin -> navigator.replaceAll(LogRegScreen())
                    is AuthEffect.NavigateToPending -> navigator.replaceAll(PendingScreen(effect.deviceId))
                    is AuthEffect.NavigateToValidate -> navigator.push(EmailVerificationScreen(effect.email, effect.deviceId))
                }
            }
        }

        LaunchedEffect(screenModel.uiMessage) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                BackPage("Hesabını Bul", {
                    screenModel.onBackClick()
                    navigator.pop()
                })
                Box(
                    modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colors.navy
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Şifrenizi mi Unuttunuz?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.navy,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Hesabınıza bağlı e-posta adresinizi girin. Size şifre sıfırlama bağlantısı göndereceğiz.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        TastyTextField(
                            value = state.pasEmail,
                            onValueChange = {
                                screenModel.onEmailEvent(EmailEvent.EmailChanged(it))
                            },
                            label = "Email",
                            leadingIcon = {
                                Icon(Icons.Default.Email, null)
                            },
                            error = state.pasEmailError
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        TastyButton(
                            text = "Şifreyi Sıfırla",
                            onClick = { screenModel.forgotPassword(state.pasEmail) },
                            isLoading = state.isLoading,
                            enabled = state.pasEmail.isNotBlank(),
                            backcolor = colors.navy,
                            textcolor = Color.White,
                            strokecolor = colors.navy
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                screenModel.onBackClick()
                                navigator.pop()
                            }
                        ) {
                            Text(
                                text = "Giriş ekranına geri dön",
                                color = colors.navy,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
                                    text = "Şifre sıfırlama bağlantısı yalnızca kayıtlı e-posta adreslerine gönderilir.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "Gönderilen doğrulama bağlantısının geçerlilik süresi 10 dakikadır.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "E-posta kutunuzda göremiyorsanız Spam klasörünü kontrol etmeyi unutmayın.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        AuthFooter()
                    }
                }
            }
        }
    }
}