package org.beem.tastymap.ui.profile.health.stepScreens
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.beem.tastymap.data.model.health.HealthEnum
import org.beem.tastymap.ui.profile.health.HealthUiState
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun SummaryStep(
    state: HealthUiState,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    var showHeader by remember { mutableStateOf(false) }
    var visibleCards by remember { mutableStateOf(0) }

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
        HealthEnum.VEGAN -> "Vegan"
        HealthEnum.VEGETARIAN -> "Vejetaryen"
        HealthEnum.NORMAL -> "Genel / Kısıtlamasız"
        null -> "Belirtilmedi"
    }

    val allergies = state.availableAllergies
        .filter { state.selectedAllergyIds.contains(it.id) }
        .joinToString(", ") { it.name }
        .ifEmpty { "Alerjim Yok" }

    // Header için animasyon değerleri (Alpha ve Scale)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 700.dp) // Web & Tablet uyumu
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Kaydırılabilir yapı
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(35.dp))


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = headerAlpha
                        scaleX = headerScale
                        scaleY = headerScale
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(customColors.gold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = customColors.gold,
                        modifier = Modifier.size(45.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Sağlık Profiliniz Hazır",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = customColors.navy
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tercihlerinizi analiz ettik.\nArtık size özel restoran ve menü önerileri sunabiliriz.",
                    fontSize = 14.sp,
                    color = customColors.navy.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(35.dp))


            AnimatedSummaryCard(
                visible = visibleCards >= 1,
                icon = Icons.Rounded.Favorite,
                title = "Diyabet Durumu",
                value = if (state.hasDiabetes) "Diyabetim Var" else "Diyabetim Yok",
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedSummaryCard(
                visible = visibleCards >= 2,
                icon = Icons.Rounded.Restaurant,
                title = "Beslenme Tercihi",
                value = eatTypeText,
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedSummaryCard(
                visible = visibleCards >= 3,
                icon = Icons.Rounded.Warning,
                title = "Alerjiler",
                value = allergies,
                customColors = customColors
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bu bilgileri profilinizden istediğiniz zaman değiştirebilirsiniz.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = customColors.navy.copy(alpha = 0.55f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.navy
                )
            ) {
                Text(
                    text = "Başlayalım",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "Bilgileri Düzenle",
                    color = customColors.navy,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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
            .background(Color.White)
            .border(
                width = 2.dp,
                color = customColors.navy.copy(alpha = 0.1f),
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
                color = customColors.navy.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.navy
            )
        }
    }
}