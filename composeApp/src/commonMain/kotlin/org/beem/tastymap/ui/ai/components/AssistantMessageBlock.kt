package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.ChatMessage
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AssistantMessageBlock(
    message: ChatMessage,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    canRetry: Boolean,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000.milliseconds)
            isCopied = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        AssistantHeaderStatus(
            isThinking = message.isLoading && message.text.isBlank(),
            statusMessage = message.statusMessage,
            customColors = customColors
        )
        // 2. Kutusuz, Doğrudan Surface Üzerinde Akan Yanıt Metni
        if (message.text.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp)
            ) {
                AiFormattedMessageText(
                    rawText = message.text,
                    places = message.places,
                    customColors = customColors,
                    onIntent = onIntent,
                    isStreaming = message.isLoading
                )
            }
        }

        // 3. Yanıt Tamamlandığında Alt Aksiyon Çubuğu (Kopyala vb.)
        AnimatedVisibility(
            visible = !message.isLoading && message.text.isNotBlank(),
            enter = fadeIn(tween(250))
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (message.text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.text))
                            isCopied = true
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                            contentDescription = "Yanıtı Kopyala",
                            tint = if (isCopied) customColors.success else customColors.textTertiary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                if (message.isError && canRetry) {
                    if (message.text.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    AiRetryPillButton(
                        customColors = customColors,
                        onClick = { onIntent(AiChatIntent.RetryLastMessage) }
                    )
                }
            }
        }
    }
}


@Composable
fun AssistantHeaderStatus(
    isThinking: Boolean,
    statusMessage: String?,
    customColors: CustomColors
) {
    val fontFamily = getAppFontFamily()

    val infiniteTransition = rememberInfiniteTransition(label = "aiThinking")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            customColors.textSecondary,
            customColors.textPrimary,
            customColors.gourmetOrange,
            customColors.textPrimary,
            customColors.textSecondary
        ),
        start = Offset(shimmerTranslate - 180f, 0f),
        end = Offset(shimmerTranslate, 0f)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(customColors.gourmetOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = customColors.gourmetOrange,
                modifier = Modifier
                    .size(15.dp)
                    .graphicsLayer {
                        if (isThinking) {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        if (isThinking) {
            val cleanStatus = (statusMessage ?: "Düşünüyor")
                .trimEnd('.', ' ')

            AnimatedContent(
                targetState = cleanStatus,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInVertically { it / 3 })
                        .togetherWith(fadeOut(tween(180)) + slideOutVertically { -it / 3 })
                },
                label = "statusTransition"
            ) { targetText ->
                Text(
                    text = targetText,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(brush = shimmerBrush)
                )
            }
        } else {
            Text(
                text = "TastyAI",
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = customColors.textSecondary
            )
        }
    }
}


@Composable
private fun AiRetryPillButton(
    customColors: CustomColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()
    val accentColor = customColors.gourmetOrange // İstersen MaterialTheme.colorScheme.error yapabilirsin

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = accentColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.30f)),
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Tekrar Dene",
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Tekrar Dene",
                fontFamily = fontFamily,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }
    }
}