package org.beem.tastymap.ui.profile.health.stepScreens

import TastyButton
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bloodtype
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.summary_allergies_none
import tastymap.composeapp.generated.resources.summary_allergies_title
import tastymap.composeapp.generated.resources.summary_diabetes_no
import tastymap.composeapp.generated.resources.summary_diabetes_title
import tastymap.composeapp.generated.resources.summary_diabetes_yes
import tastymap.composeapp.generated.resources.summary_diet_normal
import tastymap.composeapp.generated.resources.summary_diet_not_specified
import tastymap.composeapp.generated.resources.summary_diet_title
import tastymap.composeapp.generated.resources.summary_diet_vegan
import tastymap.composeapp.generated.resources.summary_diet_vegetarian
import tastymap.composeapp.generated.resources.summary_edit_btn
import tastymap.composeapp.generated.resources.summary_footer_note
import tastymap.composeapp.generated.resources.summary_start_btn
import tastymap.composeapp.generated.resources.summary_subtitle
import tastymap.composeapp.generated.resources.summary_success_badge_cd
import tastymap.composeapp.generated.resources.summary_title

@Suppress("SuspiciousIndentation")
@Composable
fun SummaryStep(
    state: HealthUiState,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    var showHeader by remember { mutableStateOf(false) }
    var visibleCards by remember { mutableStateOf(0) }
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/check.json").decodeToString()
        )
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(Unit) {
        showHeader = true
        delay(300)
        visibleCards = 1
        delay(250)
        visibleCards = 2
        delay(250)
        visibleCards = 3
    }

    val eatTypeText = when (state.selectedEatType) {
        HealthEnum.VEGAN -> stringResource(Res.string.summary_diet_vegan)
        HealthEnum.VEGETARIAN -> stringResource(Res.string.summary_diet_vegetarian)
        HealthEnum.NORMAL -> stringResource(Res.string.summary_diet_normal)
        null -> stringResource(Res.string.summary_diet_not_specified)
    }

    val selectedAllergiesList = state.availableAllergies
        .filter { state.selectedAllergyIds.contains(it.id) }
        .map { stringResource(it.nameRes) }

    val allergies = if (selectedAllergiesList.isNotEmpty()) {
        selectedAllergiesList.joinToString(", ")
    } else {
        stringResource(Res.string.summary_allergies_none)
    }

    val headerAlpha by animateFloatAsState(
        targetValue = if (showHeader) 1f else 0f,
        animationSpec = tween(600),
        label = "headerAlpha"
    )
    val headerScale by animateFloatAsState(
        targetValue = if (showHeader) 1f else 0.8f,
        animationSpec = tween(600),
        label = "headerScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = headerAlpha
                        scaleX = headerScale
                        scaleY = headerScale
                    }
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = composition,
                        progress = { progress }
                    ),
                    contentDescription = stringResource(Res.string.summary_success_badge_cd),
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = stringResource(Res.string.summary_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(Res.string.summary_subtitle),
                    fontSize = 14.sp,
                    color = customColors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            AnimatedSummaryCard(
                visible = visibleCards >= 1,
                icon = Icons.Rounded.Bloodtype,
                title = stringResource(Res.string.summary_diabetes_title),
                value = if (state.hasDiabetes) stringResource(Res.string.summary_diabetes_yes) else stringResource(Res.string.summary_diabetes_no),
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedSummaryCard(
                visible = visibleCards >= 2,
                icon = Icons.Rounded.Restaurant,
                title = stringResource(Res.string.summary_diet_title),
                value = eatTypeText,
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedSummaryCard(
                visible = visibleCards >= 3,
                icon = Icons.Rounded.HealthAndSafety,
                title = stringResource(Res.string.summary_allergies_title),
                value = allergies,
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.summary_footer_note),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = customColors.textTertiary
            )

            Spacer(modifier = Modifier.height(18.dp))

            TastyButton(
                text = stringResource(Res.string.summary_start_btn),
                onClick = onNextClick,
                isPrimary = true,
                isLoading = state.isLoading,
                backcolor = customColors.navy,
                textcolor = customColors.surface,
                strokecolor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(12.dp))

            TastyButton(
                text = stringResource(Res.string.summary_edit_btn),
                onClick = onBackClick,
                isPrimary = false,
                enabled = !state.isLoading,
                backcolor = Color.Transparent,
                textcolor = customColors.textPrimary,
                strokecolor = customColors.borderLight
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AnimatedSummaryCard(
    visible: Boolean,
    icon: ImageVector,
    title: String,
    value: String,
    customColors: CustomColors
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "cardAlpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(500),
        label = "cardOffsetY"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = offsetY
            }
            .clip(RoundedCornerShape(18.dp))
            .background(customColors.surface)
            .border(
                width = 1.5.dp,
                color = customColors.borderLight,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(customColors.gold.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customColors.gold,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.textSecondary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )
        }
    }
}