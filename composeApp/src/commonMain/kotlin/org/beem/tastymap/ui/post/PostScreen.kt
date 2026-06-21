package org.beem.tastymap.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.ui.auth.common.AuthEffect
import org.beem.tastymap.ui.auth.verification.EmailVerificationScreen
import org.beem.tastymap.ui.auth.logReg.LogRegScreen
import org.koin.compose.koinInject

class PostScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val koinInstance = koinInject<PostScreenModel>()
        val screenModel = rememberScreenModel { koinInstance }
        AuthEffectHandler(screenModel, navigator)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                screenModel.getPosts()
            }) {
                Text("Gönderileri Getir")
            }
        }

    }


    @Composable
    fun AuthEffectHandler(
        screenModel: PostScreenModel,
        navigator: Navigator
    ) {
        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToHome -> {}
                    is AuthEffect.NavigateToLogin -> { navigator.push(LogRegScreen()) }
                    is AuthEffect.NavigateToPending -> { /* ... */ }

                    is AuthEffect.NavigateToValidate -> {
                        navigator.push(EmailVerificationScreen(effect.email))
                    }
                }
            }
        }
    }

}