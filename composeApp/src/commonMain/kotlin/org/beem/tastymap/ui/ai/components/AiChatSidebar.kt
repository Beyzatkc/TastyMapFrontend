package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.core.util.iso.resolveTimeGroup
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.ai.model.AiQuotaState
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

data class ChatSessionItem(
    val sessionId: String,
    val title: String,
    val dateText: String
)

@Composable
fun AiChatSidebar(
    sessions: List<ChatSessionItem>,
    selectedSessionId: String?,
    quotaState: AiQuotaState,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val fontFamily = getAppFontFamily()
    var isQuotaExpanded by remember { mutableStateOf(false) }

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 2
        }
    }

    val groupedSessions = remember (sessions) {
        sessions
            .groupBy { resolveTimeGroup(it.dateText) }
            .toList()
            .sortedBy { (group, _) -> group.ordinal }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onIntent(AiChatIntent.LoadMoreSessions)
        }
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(customColors.surface)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // 1. ÜST BÖLÜM (COLUMN İÇİNDE BAŞLIK, KOTA VE YENİ SOHBET BUTONU)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Marka & Kota Satırı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.
                            padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TastyAI",
                            fontFamily = fontFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        )
                    }
                    HeaderQuotaPill(
                        quotaState = quotaState,
                        isExpanded = isQuotaExpanded,
                        customColors = customColors,
                        onToggle = { isQuotaExpanded = !isQuotaExpanded }
                    )
                }

                AnimatedVisibility(
                    visible = isQuotaExpanded,
                    enter = expandVertically(tween(220)) + fadeIn(tween(220)),
                    exit = shrinkVertically(tween(180)) + fadeOut(tween(150))
                ) {
                    HeaderQuotaDetailPanel(
                        quotaState = quotaState,
                        customColors = customColors,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 3. BALONCUKSUZ, TEMİZ OTURUM LİSTESİ
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz sohbet geçmişin yok.",
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        color = customColors.textSecondary.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item(
                        key = "new_chat"
                    ) {
                        val isNewChatSelected = selectedSessionId == null

                        val newChatBgColor by animateColorAsState(
                            targetValue = if (isNewChatSelected) {
                                customColors.surfaceVariant
                            } else {
                                Color.Transparent
                            },
                            animationSpec = tween(180)
                        )
                        Surface(
                            onClick = { onIntent(AiChatIntent.NewChat) },
                            shape = RoundedCornerShape(12.dp),
                            color = newChatBgColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, top = 11.dp, bottom = 11.dp),//, horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EditNote,
                                    contentDescription = null,
                                    tint = customColors.textPrimary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "Yeni sohbet",
                                    fontFamily = fontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = if (isNewChatSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = customColors.textPrimary
                                )
                            }
                        }
                    }
                    groupedSessions.forEach { (group, groupSessions) ->
                        item(
                            key =  "header_${group.name}"
                        ) {
                            Text(
                                text = group.label,
                                fontFamily = fontFamily,
                                fontSize = 11.5.sp,
                                letterSpacing = 0.8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = customColors.textSecondary.copy(alpha = 0.75f),
                                modifier = Modifier.padding(
                                    start = 12.dp,
                                    top = if (group == groupedSessions.first().first) 4.dp else 16.dp,
                                    bottom = 2.dp
                                )
                            )
                        }
                        items(groupSessions, key = { it.sessionId }) { session ->
                            val isSelected = session.sessionId == selectedSessionId
                            SessionHistoryRow(
                                session = session,
                                isSelected = isSelected,
                                customColors = customColors,
                                onSelect = { onIntent(AiChatIntent.SelectSession(session.sessionId)) },
                                onDelete = { onIntent(AiChatIntent.DeleteSession(session.sessionId)) }
                            )
                        }
                    }
                }
            }
        }

        // Sağ kenarda ana sohbet alanından ayıran ince çizgi
        VerticalDivider(
            thickness = 0.5.dp,
            color = customColors.borderLight
        )
    }
}