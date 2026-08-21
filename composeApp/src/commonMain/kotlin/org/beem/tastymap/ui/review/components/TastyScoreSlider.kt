package org.beem.tastymap.ui.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.AppColors
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastyScoreSlider(
    title: String,
    score: Double, // 0.0 - 5.0
    onScoreChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEvaluated = score > 0.0
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Başlık ve Sabit Genişlikli Rozet
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isEvaluated) FontWeight.Bold else FontWeight.Medium,
                color = if (isEvaluated) AppColors.NavyBlue else AppColors.DarkGrayLines
            )

            // Layout shift yaratmayan sabit genişlikli rozet slotu
            Surface(
                color = if (isEvaluated) AppColors.NavyBlue else AppColors.LightGray,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isEvaluated) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AppColors.Gold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = score.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "-",
                            color = AppColors.DarkGrayLines,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Dokunulabilir ve Sürüklenebilir Gerçek Slider
        Slider(
            value = score.toFloat(),
            onValueChange = { rawValue ->
                // 0.5 adımlarla yuvarla
                val stepped = (rawValue * 2).roundToInt() / 2.0
                onScoreChange(stepped)
            },
            valueRange = 0f..5f,
            steps = 9, // 0.0, 0.5, 1.0, 1.5 ... 5.0
            interactionSource = interactionSource,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .shadow(elevation = 3.dp, shape = CircleShape)
                        .background(Color.White, shape = CircleShape)
                        .border(
                            width = 2.5.dp,
                            color = if (isEvaluated) AppColors.NavyBlue else AppColors.DarkGrayLines.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(4.dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = AppColors.NavyBlue,
                        inactiveTrackColor = AppColors.WaveColor,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
        )
    }
}