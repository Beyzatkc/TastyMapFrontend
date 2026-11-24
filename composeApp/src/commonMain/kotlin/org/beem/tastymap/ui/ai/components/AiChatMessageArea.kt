package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.ChatMessage
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@Composable
fun AiChatMessageArea(
    messages: List<ChatMessage>,
    isMessagesLoading: Boolean,
    messagesLoadError: String?,
    isLoadingOlderMessages: Boolean,
    olderMessagesLoadError: String?,
    hasMoreOlderMessages: Boolean,
    isStreaming: Boolean,
    hasPreviewPlace: Boolean,
    listState: LazyListState,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val isWideScreen = LocalIsWideScreen.current
    val horizontalPadding = if (isWideScreen) 80.dp else 16.dp

    val shouldLoadMore by remember(hasMoreOlderMessages, isLoadingOlderMessages, olderMessagesLoadError) {
        derivedStateOf {
            hasMoreOlderMessages &&
                    !isLoadingOlderMessages &&
                    olderMessagesLoadError == null &&
                    listState.firstVisibleItemIndex <= 2 &&
                    listState.layoutInfo.totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onIntent(AiChatIntent.LoadOlderMessages)
        }
    }

    var previousFirstMessageId by remember { mutableStateOf(messages.firstOrNull()?.id) }
    var previousSize by remember { mutableStateOf(messages.size) }

    LaunchedEffect(messages.firstOrNull()?.id, messages.size) {
        val currentFirstId = messages.firstOrNull()?.id
        val addedToTop = messages.size - previousSize

        val isPrepended = addedToTop > 0 &&
                previousFirstMessageId != null &&
                currentFirstId != previousFirstMessageId &&
                messages.getOrNull(addedToTop)?.id == previousFirstMessageId

        if (isPrepended) {
            val currentIndex = listState.firstVisibleItemIndex
            val currentOffset = listState.firstVisibleItemScrollOffset
            listState.scrollToItem(currentIndex + addedToTop, currentOffset)
        }

        previousFirstMessageId = currentFirstId
        previousSize = messages.size
    }

    when {
        isMessagesLoading -> {
            ChatMessagesSkeleton(
                modifier = modifier.padding(horizontal = horizontalPadding, vertical = 20.dp)
            )
        }

        messagesLoadError != null && messages.isEmpty() -> {
            MessagesLoadErrorCard(
                message = messagesLoadError,
                onRetry = { onIntent(AiChatIntent.RetryLoadMessages) },
                modifier = modifier
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = 80.dp)
            )
        }

        messages.isEmpty() -> {
            AiWelcomeView(
                customColors = customColors,
                onIntent = onIntent,
                modifier = modifier.padding(bottom = 80.dp)
            )
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = modifier.padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = if (hasPreviewPlace) 240.dp else 100.dp
                )
            ) {
                when {
                    isLoadingOlderMessages -> {
                        item(key = "older_messages_loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = customColors.gourmetOrange,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    olderMessagesLoadError != null -> {
                        item(key = "older_messages_error") {
                            OlderMessageLoadErrorCard(
                                olderMessagesLoadError,
                                onRetry = { onIntent(AiChatIntent.LoadOlderMessages) }
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = messages,
                    key = { _, msg -> msg.id }
                ) { index, message ->
                    AiChatBubble(
                        message = message,
                        customColors = customColors,
                        onIntent = onIntent,
                        canRetry = (index == messages.lastIndex) && !isStreaming
                    )
                }
            }
        }
    }
}

@Composable
private fun MessagesLoadErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.WifiOff,
                contentDescription = null,
                tint = customColors.textSecondary,
                modifier = Modifier.size(44.dp)
            )

            Text(
                text = "Sohbet yüklenemedi",
                fontFamily = fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = customColors.textPrimary
            )

            Text(
                text = message,
                fontFamily = fontFamily,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = customColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 300.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Surface(
                onClick = onRetry,
                shape = RoundedCornerShape(50),
                color = customColors.surfaceVariant,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Tekrar Dene",
                        tint = customColors.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tekrar Dene",
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = customColors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessagesSkeleton(
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "chatSkeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    val shimmerColor = customColors.surfaceVariant.copy(alpha = alpha)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Sağda Kullanıcı Balonu İskeleti
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 6.dp))
                    .background(shimmerColor)
            )
        }

        // 2. Solda Asistan Yanıtı İskeleti
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(shimmerColor)
                )
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColor)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerColor)
            )
        }
    }
}

@Composable
private fun OlderMessageLoadErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val fontFamily = getAppFontFamily()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = errorMessage,
            fontFamily = fontFamily,
            fontSize = 12.5.sp,
            color = customColors.textSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            onClick = onRetry,
            shape = RoundedCornerShape(50),
            color = customColors.surfaceVariant,
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
        ) {
            Text(
                text = "Tekrar dene",
                fontFamily = fontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = customColors.textPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}