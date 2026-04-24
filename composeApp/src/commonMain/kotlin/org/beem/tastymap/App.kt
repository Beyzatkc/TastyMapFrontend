package org.beem.tastymap

import TastyButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import org.beem.tastymap.ui.auth.LoginScreen
import org.beem.tastymap.ui.components.TastyTextField
import org.beem.tastymap.ui.theme.TastyTheme
import org.jetbrains.compose.resources.painterResource

import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    var isDark by remember { mutableStateOf(false) }

    TastyTheme(useDarkTheme = isDark) {
        Navigator(LoginScreen())

    }
}