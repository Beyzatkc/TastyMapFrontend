package org.beem.tastymap.ui.review.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.AppColors
import kotlin.math.roundToInt

@Composable
fun TastyRatingBar(
    rating: Double,
    onRatingChange: (Double) -> Unit,
    onRatingSelected: ((Double) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val description = when {
        rating >= 5.0 -> "Unutulmaz bir lezzet!"
        rating >= 4.0 -> "Çok lezzetli"
        rating >= 3.0 -> "Gayet iyi"
        rating >= 2.0 -> "Daha iyi olabilirdi"
        rating >= 1.0 -> "Beklentimi karşılamadı"
        rating >= 0.5 -> "Çok kötü"
        else -> "Lezzetini değerlendir (Kaydır veya Dokun)"
    }

    var rowWidthPx by remember { mutableStateOf(1f) }
    var currentRating by remember(rating) { mutableStateOf(rating) }

    fun calculateScore(xPos: Float): Double {
        val fraction = (xPos / rowWidthPx).coerceIn(0f, 1f)
        val rawScore = fraction * 5.0
        val steppedScore = (rawScore * 2).roundToInt() / 2.0
        return steppedScore.coerceIn(0.5, 5.0)
    }

    fun updateRatingFromPosition(xPos: Float) {
        val fraction = (xPos / rowWidthPx).coerceIn(0f, 1f)
        val rawScore = fraction * 5.0
        val steppedScore = (rawScore * 2).roundToInt() / 2.0
        onRatingChange(steppedScore.coerceIn(0.5, 5.0))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .onSizeChanged { rowWidthPx = it.width.toFloat() }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val score = calculateScore(offset.x)
                        currentRating = score
                        onRatingChange(score)
                        onRatingSelected?.invoke(score)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val score = calculateScore(offset.x)
                            currentRating = score
                            onRatingChange(score)
                        },
                        onDrag = { change, _ ->
                            val score = calculateScore(change.position.x)
                            currentRating = score
                            onRatingChange(score)
                        },
                        onDragEnd = {
                            onRatingSelected?.invoke(currentRating)
                        },
                        onDragCancel = {
                            onRatingSelected?.invoke(currentRating)
                        }
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { index ->
                val fillFraction = (rating - (index - 1)).coerceIn(0.0, 1.0).toFloat()
                TastyPinItem(fillFraction = fillFraction)
            }
        }

        Text(
            text = description,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (rating > 0.0) AppColors.NavyBlue else AppColors.DarkGrayLines
        )
    }
}