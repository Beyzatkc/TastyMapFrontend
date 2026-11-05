package org.beem.tastymap.ui.map.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.map.states.SearchChipState
import org.beem.tastymap.ui.theme.AppColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
actual fun SearchThisAreaChip(
    state: SearchChipState,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val fontFamily = getAppFontFamily()
    val isVisible = state != SearchChipState.HIDDEN
    val isInteractive = state == SearchChipState.ACTIONABLE

    val tintColor by animateColorAsState(
        targetValue = when (state) {
            SearchChipState.SUCCESS -> AppColors.SuccessGreen
            SearchChipState.ERROR -> AppColors.ErrorRed
            else -> AppColors.GourmetOrange
        },
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            SearchChipState.SUCCESS -> AppColors.SuccessGreen.copy(alpha = 0.5f)
            SearchChipState.ERROR -> AppColors.ErrorRed.copy(alpha = 0.5f)
            else -> AppColors.BorderLight
        },
        animationSpec = tween(300)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = modifier
    ) {
        Surface(
            onClick = { if (isInteractive) onClick() },
            enabled = isInteractive,
            shape = RoundedCornerShape(24.dp),
            color = AppColors.Surface,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // İkon Animasyonu
                AnimatedContent(
                    targetState = state,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "ChipIconAnimation"
                ) { targetState ->
                    when (targetState) {
                        SearchChipState.LOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = tintColor
                            )
                        }
                        SearchChipState.SUCCESS -> {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Başarılı",
                                tint = tintColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        SearchChipState.ERROR -> {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = "Hata",
                                tint = tintColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Alanı Yenile",
                                tint = tintColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Metin Animasyonu
                AnimatedContent(
                    targetState = state,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "ChipTextAnimation"
                ) { targetState ->
                    val text = when (targetState) {
                        SearchChipState.LOADING -> "Mekanlar Aranıyor..."
                        SearchChipState.SUCCESS -> "Bölge Tarandı"
                        SearchChipState.ERROR -> "Mekanlar Alınamadı"
                        else -> "Bu Bölgede Ara"
                    }

                    val textColor = when (targetState) {
                        SearchChipState.SUCCESS -> AppColors.SuccessGreen
                        SearchChipState.ERROR -> AppColors.ErrorRed
                        else -> AppColors.TextPrimary
                    }

                    Text(
                        text = text,
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}