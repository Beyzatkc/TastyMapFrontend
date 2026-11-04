package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiMessageInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isStreaming: Boolean,
    isWideScreen: Boolean,
    customColors: CustomColors
) {
    Surface(
        color = customColors.darkHeaderColor,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isWideScreen) 64.dp else 12.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = onInputTextChange,
                placeholder = {
                    Text(
                        text = "Nerede ne yemek istersin? (örn. En iyi dönerci)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = customColors.textPrimary.copy(alpha = 0.5f)
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = customColors.surfaceVariant,
                    unfocusedContainerColor = customColors.surfaceVariant,
                    disabledContainerColor = customColors.surfaceVariant,
                    focusedTextColor = customColors.textPrimary,
                    unfocusedTextColor = customColors.textPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4,
                enabled = !isStreaming
            )

            Spacer(modifier = Modifier.width(8.dp))

            val canSend = inputText.isNotBlank() && !isStreaming
            IconButton(
                onClick = onSendClick,
                enabled = canSend,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) customColors.gourmetOrange
                        else customColors.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gönder",
                    tint = if (canSend) Color.White else customColors.textPrimary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}