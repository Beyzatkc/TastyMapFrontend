package org.beem.tastymap.ui.profile.health

import TastyButton
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.beem.tastymap.ui.profile.health.HealthScreenModel
import org.beem.tastymap.ui.profile.health.HealthWizardScreen
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.onboarding_feature_healthy_desc
import tastymap.composeapp.generated.resources.onboarding_feature_healthy_title
import tastymap.composeapp.generated.resources.onboarding_feature_original_desc
import tastymap.composeapp.generated.resources.onboarding_feature_original_title
import tastymap.composeapp.generated.resources.onboarding_feature_smart_desc
import tastymap.composeapp.generated.resources.onboarding_feature_smart_title
import tastymap.composeapp.generated.resources.onboarding_skip_btn
import tastymap.composeapp.generated.resources.onboarding_start_btn
import tastymap.composeapp.generated.resources.onboarding_subtitle
import tastymap.composeapp.generated.resources.onboarding_tagline
import tastymap.composeapp.generated.resources.onboarding_welcome

class OnBoardingScreen : Screen {

    @Composable
    override fun Content() {
        var showContent by remember { mutableStateOf(false) }
        val screenModel = koinScreenModel<HealthScreenModel>()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            showContent = true
        }

        val customColors = LocalCustomColors.current
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customColors.background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(800)) +
                        slideInVertically(
                            initialOffsetY = { it / 10 },
                            animationSpec = tween(800, easing = EaseOutCubic)
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .widthIn(max = 440.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WelcomeLogo(customColors = customColors)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.onboarding_subtitle),
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            color = customColors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    WelcomeCardContent(
                        customColors = customColors,
                        navigator = navigator,
                        screenModel = screenModel
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun WelcomeLogo(customColors: CustomColors) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.onboarding_welcome),
            color = customColors.textTertiary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TastyMap",
                color = customColors.textPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = customColors.yellow,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AI",
                    color = customColors.surface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = customColors.yellow.copy(alpha = 0.12f),
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(1.2.dp, customColors.yellow.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = customColors.yellow,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(Res.string.onboarding_tagline),
                    color = customColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WelcomeCardContent(
    customColors: CustomColors,
    navigator: Navigator,
    screenModel: HealthScreenModel
) {
    Card(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false,
                ambientColor = customColors.textPrimary.copy(alpha = 0.08f),
                spotColor = customColors.textPrimary.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.Psychology,
                    title = stringResource(Res.string.onboarding_feature_smart_title),
                    description =stringResource(Res.string.onboarding_feature_smart_desc),
                    modifier = Modifier.weight(1f)
                )
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.HealthAndSafety,
                    title = stringResource(Res.string.onboarding_feature_healthy_title),
                    description = stringResource(Res.string.onboarding_feature_healthy_desc),
                    modifier = Modifier.weight(1f)
                )
                FeatureColumn(
                    customColors = customColors,
                    icon = Icons.Rounded.Restaurant,
                    title = stringResource(Res.string.onboarding_feature_original_title),
                    description = stringResource(Res.string.onboarding_feature_original_desc),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            TastyButton(
                text = stringResource(Res.string.onboarding_start_btn),
                onClick = { navigator.push(HealthWizardScreen()) },
                isPrimary = true,
                backcolor = customColors.navy,
                textcolor = customColors.surface,
                strokecolor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(12.dp))

            TastyButton(
                text = stringResource(Res.string.onboarding_skip_btn),
                onClick = screenModel::skipHealthWizard,
                isPrimary = false,
                backcolor = Color.Transparent,
                textcolor = customColors.textSecondary,
                strokecolor = customColors.borderLight
            )
        }
    }
}

@Composable
fun FeatureColumn(
    customColors: CustomColors,
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = customColors.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customColors.navy,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = customColors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = customColors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )
    }
}