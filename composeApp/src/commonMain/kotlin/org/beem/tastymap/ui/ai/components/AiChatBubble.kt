package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.ChatMessage
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiChatBubble(
    message: ChatMessage,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.isFromUser

    // Düşünme durumu için hafif nefes alma (pulse) animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "thinkingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) customColors.gourmetOrange else customColors.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (isUser) {
                    SelectionContainer {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                            )
                        )
                    }
                } else {
                    // ASİSTAN TARAFI
                    if (message.isLoading && message.text.isBlank()) {
                        // Henüz token gelmedi; Gemini/ChatGPT gibi baloncuk içi düşünme durumu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .alpha(pulseAlpha)
                                .padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = customColors.gourmetOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = message.statusMessage ?: "Düşünüyor...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = customColors.textPrimary
                                )
                            )
                        }
                    } else {
                        // Tokenlar akmaya başladı veya tamamlandı
                        AiFormattedMessageText(
                            rawText = message.text,
                            places = message.places,
                            customColors = customColors,
                            onIntent = onIntent
                        )
                    }
                }
            }
        }
    }
}