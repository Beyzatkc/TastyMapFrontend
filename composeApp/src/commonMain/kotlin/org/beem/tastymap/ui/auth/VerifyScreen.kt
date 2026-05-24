package org.beem.tastymap.ui.auth

import TastyButton
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import kotlinx.coroutines.delay
import org.beem.tastymap.core.constants.AuthConstants.MSG_VERIFICATION_FINISHED
import org.beem.tastymap.core.navigation.DeepLinkManager
import org.beem.tastymap.core.navigation.PlatformMessenger
import org.beem.tastymap.ui.animations.TastyAnimations.scaleFade
import org.koin.compose.koinInject

class VerifyScreen(val token: String) : Screen{

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<AuthScreenModel>()
        val messenger = koinInject<PlatformMessenger>()
        val screenModel = rememberScreenModel { koinInstance }
        val state by screenModel.verificationState.collectAsState()


        LaunchedEffect(state.isEmailVerified) {
            if (state.isEmailVerified) {
                messenger.post(MSG_VERIFICATION_FINISHED)
                delay(2000)

                if (navigator.canPop) {
                    navigator.pop()
                } else {
                    navigator.replaceAll(LogRegScreen())
                }
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                DeepLinkManager.clear()
            }
        }

        LaunchedEffect(Unit) {
            screenModel.verifyEmail(token)
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state.verificationError != null,
                transitionSpec = {
                    scaleFade()
                },
                label = "VerifyStateAnim"
            ) { isError ->

                Column(
                    modifier = Modifier
                        .widthIn(max = 440.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isError) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(54.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Hesabınız Doğrulanıyor",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Lütfen bekleyiniz, giriş sayfasına yönlendiriliyorsunuz.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }else {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 440.dp)
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(56.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Bir Sorun Oluştu",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = state.verificationError ?: "Beklenmedik bir hata ile karşılaşıldı. Lütfen internet bağlantınızı kontrol edip tekrar deneyiniz.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                TastyButton(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f),
                                    text = "Geri Dön",
                                    onClick = {
                                        if(state.isEmailVerified && state.isLogin ){
                                            //navigator.replaceAll(HomeScreen())
                                        }else{
                                            if (navigator.canPop) {
                                                navigator.pop()
                                            } else {
                                                navigator.replaceAll(LogRegScreen())
                                            }
                                        }
                                    },
                                    backcolor = MaterialTheme.colorScheme.error,
                                    textcolor = Color.White,
                                    strokecolor = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}