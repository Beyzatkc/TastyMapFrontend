package org.beem.tastymap.ui.ai.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.ChatMessage
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiChatBubble(
    message: ChatMessage,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    canRetry: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (message.isFromUser) {
        UserMessageBubble(
            text = message.text,
            customColors = customColors,
            modifier = modifier
        )
    } else {
        AssistantMessageBlock(
            message = message,
            customColors = customColors,
            onIntent = onIntent,
            canRetry = canRetry,
            modifier = modifier
        )
    }
}