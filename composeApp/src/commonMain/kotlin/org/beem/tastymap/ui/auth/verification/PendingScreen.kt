package org.beem.tastymap.ui.auth.verification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.model.ApprovedRefreshRequestDTO
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.common.AuthLifecycleEvent
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.splash.SplashScreen
import org.beem.tastymap.ui.theme.LocalCustomColors

class PendingScreen(val deviceId: String) : Screen {
    @OptIn(InternalVoyagerApi::class)
    @Preview
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<PendingScreenModel>()
        val lifecycleOwner = LocalLifecycleOwner.current
        val navigator = LocalNavigator.currentOrThrow
        val colors = LocalCustomColors.current
        val materialColors = MaterialTheme.colorScheme
        val state by screenModel.sendState.collectAsState()

        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            screenModel.pendingLogin.collect { pendingLogin ->
                when (pendingLogin) {
                    is AuthEffect.NavigateToHome -> { navigator.replaceAll(SplashScreen()) }
                    is AuthEffect.NavigateToLogin -> { navigator.replaceAll(LogRegScreen()) }
                    is AuthEffect.NavigateToPending -> {
                        navigator.replaceAll(PendingScreen(pendingLogin.deviceId))
                    }
                    is AuthEffect.NavigateToValidate -> {
                        navigator.push(EmailVerificationScreen(pendingLogin.email,pendingLogin.deviceId))
                    }
                }
            }
        }

        LaunchedEffect(state.error) {
            state.error?.let { errorMessage ->
                if (errorMessage.isNotEmpty()) {
                    ToastManager.show(errorMessage)
                }
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        println("LIFECYCLE: RESUME pendıngscreen")
                        screenModel.onLifecycleEvent(
                            AuthLifecycleEvent.Resume,
                            deviceId
                        )
                    }
                    Lifecycle.Event.ON_STOP -> {
                        screenModel.onLifecycleEvent(
                            AuthLifecycleEvent.Stop,
                            deviceId
                        )
                    }
                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        BackHandler(enabled = true) { }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = materialColors.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = colors.navy.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = colors.navy
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Güvenlik Doğrulaması",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Hesabınızın güvenliğini sağlamak için bu giriş denemesi ek doğrulama gerektirmektedir.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = materialColors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = materialColors.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = colors.navy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Doğrulama e-postası gönderildi",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Kayıtlı e-posta adresinize doğrulama bağlantısı gönderildi. Onay işlemi tamamlandığında bu ekran otomatik olarak güncellenecektir.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.5.dp,
                        color = colors.navy
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Doğrulama bekleniyor...",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = materialColors.errorContainer
                        )
                    ) {
                        Text(
                            text = "Lütfen bu ekranı kapatmayın. Onay işlemi tamamlandığında giriş işleminiz otomatik olarak devam edecektir.",
                            modifier = Modifier.padding(12.dp),
                            color = materialColors.onErrorContainer,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    ResendSection(
                        navyIcons = colors.navy,
                        screenModel = screenModel,
                        onResendClick = { screenModel.resendEmail(deviceId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ResendSection(
    onResendClick: () -> Unit,
    navyIcons: Color,
    screenModel: PendingScreenModel
) {
    val timeLeft by screenModel.timeLeft.collectAsState()
    val isButtonEnabled = timeLeft == 0

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        TextButton(
            enabled = isButtonEnabled,
            onClick = {
                if (isButtonEnabled) {
                    screenModel.startTimer()
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