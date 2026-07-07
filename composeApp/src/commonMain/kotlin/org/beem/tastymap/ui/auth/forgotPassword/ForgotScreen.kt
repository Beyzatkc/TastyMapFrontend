package org.beem.tastymap.ui.auth.forgotPassword

import TastyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.beem.tastymap.ui.auth.splash.SplashScreen
import org.beem.tastymap.ui.auth.verification.EmailVerificationScreen
import org.beem.tastymap.ui.auth.verification.PendingScreen
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
        val materialColors = MaterialTheme.colorScheme

        // Kaydırma durumunu hafızada tutan state
        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToHome -> { navigator.replaceAll(SplashScreen()) }
                    is AuthEffect.NavigateToLogin -> { navigator.replaceAll(LogRegScreen()) }
                    is AuthEffect.NavigateToPending -> {
                        navigator.replaceAll(PendingScreen(effect.deviceId))
                    }
                    is AuthEffect.NavigateToValidate -> {
                        navigator.push(EmailVerificationScreen(effect.email, effect.deviceId))
                    }
                }
            }
        }

        LaunchedEffect(screenModel.uiMessage) {
            screenModel.uiMessage.collect { message ->
                ToastManager.show(message)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 24.dp)
            ) {

                IconButton(
                    onClick = {
                        screenModel.onBackClick()
                        navigator.pop()
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = colors.navy
                    )
                }

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            colors.navy.copy(alpha = .08f),
                            RoundedCornerShape(28.dp)
                        )
                        .size(96.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = colors.navy
                    )
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Şifrenizi mi Unuttunuz?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.navy
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Hesabınıza bağlı e-posta adresinizi girin. Size şifre sıfırlama bağlantısı göndereceğiz.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(36.dp))

                TastyTextField(
                    value = state.pasEmail,
                    onValueChange = {
                        screenModel.onEmailEvent(
                            EmailEvent.EmailChanged(it)
                        )
                    },
                    label = "Email",
                    leadingIcon = {
                        Icon(Icons.Default.Email, null)
                    },
                    error = state.pasEmailError
                )

                Spacer(Modifier.height(24.dp))

                TastyButton(
                    text = "Şifreyi Sıfırla",
                    onClick = {
                        screenModel.forgotPassword(state.pasEmail)
                    },
                    isLoading = state.isLoading,
                    enabled = state.pasEmail.isNotBlank(),
                    backcolor = colors.navy,
                    textcolor = Color.White,
                    strokecolor = colors.navy
                )

                // İçeriği aşağı iter
                Spacer(modifier = Modifier.height(120.dp))

                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        screenModel.onBackClick()
                        navigator.pop()
                    }
                ) {
                    Text(
                        text = "Giriş ekranına geri dön",
                        color = colors.navy
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}