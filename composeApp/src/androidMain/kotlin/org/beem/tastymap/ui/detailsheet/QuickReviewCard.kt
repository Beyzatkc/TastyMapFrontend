package org.beem.tastymap.ui.detailsheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.review.components.TastyPinItem
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import kotlin.math.roundToInt

@Composable
fun QuickReviewCard(
    userName: String = "Sen",
    userAvatarUrl: String? = null,
    score: Double,
    onScoreChange: (Double) -> Unit,
    onScoreSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()
    var rowWidthPx by remember { mutableStateOf(1f) }
    var tempScore by remember(score) { mutableStateOf(score) }

    fun calculateScore(xPos: Float): Double {
        val fraction = (xPos / rowWidthPx).coerceIn(0f, 1f)
        val stepped = ((fraction * 5.0) * 2).roundToInt() / 2.0
        return stepped.coerceIn(0.5, 5.0)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        border = BorderStroke(1.dp, AppColors.BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Kullanıcı Profili ve Puan Rozeti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AppColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = userName,
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Deneyimini puanlamak için kaydır",
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            color = AppColors.TextTertiary
                        )
                    }
                }

                // Seçilen Puan Rozeti
                if (tempScore > 0.0) {
                    Surface(
                        color = AppColors.WarmAmber,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$tempScore",
                            fontFamily = fontFamily,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 2. Tam Genişlikte 5'li Pin Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { rowWidthPx = it.width.toFloat() }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val s = calculateScore(offset.x)
                            tempScore = s
                            onScoreChange(s)
                            onScoreSelected(s)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val s = calculateScore(offset.x)
                                tempScore = s
                                onScoreChange(s)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val s = calculateScore(change.position.x)
                                tempScore = s
                                onScoreChange(s)
                            },
                            onDragEnd = { onScoreSelected(tempScore) },
                            onDragCancel = { onScoreSelected(tempScore) }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { index ->
                    val fillFraction = (tempScore - (index - 1)).coerceIn(0.0, 1.0).toFloat()
                    TastyPinItem(
                        fillFraction = fillFraction,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    }
}