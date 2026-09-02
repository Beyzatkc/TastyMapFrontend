package org.beem.tastymap.ui.auth.verification.email

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.beem.tastymap.ui.components.AuthFooter
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.verification_success_desc
import tastymap.composeapp.generated.resources.verification_success_icon_cd
import tastymap.composeapp.generated.resources.verification_success_title

class VerificationSuccessScreen : Screen {
    @Composable
    override fun Content() {
        val colors = LocalCustomColors.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(colors.green.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(Res.string.verification_success_icon_cd),
                            modifier = Modifier.size(64.dp),
                            tint = colors.green
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = stringResource(Res.string.verification_success_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.verification_success_desc),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        lineHeight = 24.sp,
                        modifier = Modifier.widthIn(max = 350.dp)
                    )
                }
            }

            AuthFooter(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}