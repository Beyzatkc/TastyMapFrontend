package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.AiQuotaState
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiFloatingInputBox(
    inputText: String,
    isStreaming: Boolean,
    quotaState: AiQuotaState,
    isWideScreen: Boolean,
    customColors: CustomColors,
    enabled: Boolean,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isInputEnabled = enabled && !isStreaming
    val canSend = isInputEnabled && quotaState.hasQuota && inputText.isNotBlank()
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = inputText, selection = TextRange(inputText.length)))
    }

    // ViewModel dışarıdan metni temizlediğinde (örn. mesaj gönderildiğinde) senkronize et
    LaunchedEffect(inputText) {
        if (inputText != textFieldValue.text) {
            textFieldValue = TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isWideScreen) 96.dp else 16.dp,
                vertical = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = customColors.surfaceVariant,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
                .alpha(if (enabled) 1f else 0.55f)
        ) {
            AnimatedContent(
                targetState = quotaState.hasQuota,
                transitionSpec = {
                    fadeIn(tween(220)).togetherWith(fadeOut(tween(180)))
                },
                label = "inputQuotaTransition"
            ) { hasAvailableQuota ->
                if (!hasAvailableQuota) {
                    QuotaExhaustedBar(
                        dailyLimit = quotaState.dailyLimit,
                        customColors = customColors,
                        onUpgradeClick = { onIntent(AiChatIntent.ShowUpgradeSheet) }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                textFieldValue = newValue
                                if (newValue.text != inputText) {
                                    onIntent(AiChatIntent.InputTextChanged(newValue.text))
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (canSend) onIntent(AiChatIntent.SendMessage)
                                }
                            ),
                            placeholder = {
                                Text(
                                    text = "Konya'da akşam için nereye gidelim?",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = customColors.textSecondary.copy(alpha = 0.7f)
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 40.dp, max = 100.dp)
                                .onPreviewKeyEvent{ keyEvent ->
                                    val isEnterKey = keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter
                                    when {
                                        isEnterKey && keyEvent.isShiftPressed -> {
                                            if (keyEvent.type == KeyEventType.KeyDown && isInputEnabled) {
                                                val currentText = textFieldValue.text
                                                val selStart = textFieldValue.selection.min
                                                val selEnd = textFieldValue.selection.max
                                                val updatedText = currentText.replaceRange(selStart, selEnd, "\n")
                                                val newCursor = selStart + 1

                                                textFieldValue = TextFieldValue(
                                                    text = updatedText,
                                                    selection = TextRange(newCursor)
                                                )
                                                onIntent(AiChatIntent.InputTextChanged(updatedText))
                                            }
                                            true
                                        }
                                        isEnterKey && !keyEvent.isShiftPressed -> {
                                            if (keyEvent.type == KeyEventType.KeyDown && canSend) {
                                                onIntent(AiChatIntent.SendMessage)
                                            }
                                            true
                                        }
                                        else -> false
                                    }
                                },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedTextColor = customColors.textPrimary,
                                unfocusedTextColor = customColors.textPrimary,
                                cursorColor = customColors.gourmetOrange,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 3,
                            enabled = isInputEnabled
                        )

                        when {
                            isStreaming -> {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(customColors.textPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(customColors.textPrimary)
                                    )
                                }
                            }
                            canSend -> {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(customColors.gourmetOrange)
                                        .clickable { onIntent(AiChatIntent.SendMessage) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Gönder",
                                        tint = Color.White,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}