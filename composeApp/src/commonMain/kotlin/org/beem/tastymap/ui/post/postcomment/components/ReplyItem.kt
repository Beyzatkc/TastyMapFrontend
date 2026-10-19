package org.beem.tastymap.ui.post.postcomment.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.beem.tastymap.core.util.formatToShortRelativeTime
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import org.beem.tastymap.ui.components.MentionFormattedText
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.reply_action

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyItem(
    reply: CommentsResponse,
    isWeb: Boolean,
    checkIsOwnComment: (Long) -> Boolean,
    onUserClick: () -> Unit,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onToggleLike: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var showMenu by remember { mutableStateOf(false) }

    val isOwnReply = checkIsOwnComment(reply.userId)

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var baseModifier = Modifier.fillMaxWidth()
    if (!isWeb) {
        baseModifier = baseModifier.combinedClickable(
            onLongClick = { showMenu = true },
            onClick = {}
        )
    } else {
        baseModifier = baseModifier.hoverable(interactionSource)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = baseModifier
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(customColors.surfaceVariant)
                    .clickable { onUserClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!reply.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = reply.profilePhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = customColors.placeHolderIcon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = reply.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = customColors.textPrimary), modifier = Modifier.clickable { onUserClick() })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formatToShortRelativeTime(reply.date), style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary))

                    if (reply.updateDate != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(Düzenlendi)",
                            style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary),
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (isWeb && isHovered) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Seçenekler", tint = customColors.textSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                MentionFormattedText(text = reply.contents)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.reply_action),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = customColors.textSecondary
                    ),
                    modifier = Modifier.clickable { onReplyClick() }
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { onToggleLike() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (reply.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (reply.isLiked) customColors.red else customColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (reply.numberOfLikes > 0) {
                    Text(
                        text = reply.numberOfLikes.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(color = customColors.textSecondary)
                    )

                }
            }
        }

        CommentActionDropdownMenu(
            expanded = showMenu,
            isOwnComment = isOwnReply,
            showPinOption = false,
            onDismissRequest = { showMenu = false },
            onUpdateClick = {
                showMenu = false
                onUpdateClick()
            },
            onDeleteClick = {
                showMenu = false
                onDeleteClick()
            },
            onReportClick = {
                showMenu = false
                // TODO: Şikayet et
            }
        )
    }
}