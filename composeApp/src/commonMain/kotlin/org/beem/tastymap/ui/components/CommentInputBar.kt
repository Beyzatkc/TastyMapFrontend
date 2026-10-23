package org.beem.tastymap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.add_comment_placeholder
import tastymap.composeapp.generated.resources.add_reply_placeholder
import tastymap.composeapp.generated.resources.cancel_reply_content_desc
import tastymap.composeapp.generated.resources.profile_share
import tastymap.composeapp.generated.resources.replying_to_label
import tastymap.composeapp.generated.resources.share_post

@Composable
fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    profilePhotoUrl: String? = null,
    replyingToUsername: String? = null,
    onCancelReply: () -> Unit = {},
    isLoading: Boolean = false
) {
    val customColors = LocalCustomColors.current

    var cachedUsername by remember { mutableStateOf("") }

    LaunchedEffect(replyingToUsername) {
        if (replyingToUsername != null) {
            cachedUsername = replyingToUsername
        }
    }

    val dynamicPlaceholder = if (replyingToUsername != null) {
        stringResource(Res.string.add_reply_placeholder)
    } else {
        stringResource(Res.string.add_comment_placeholder)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(customColors.surface)
    ) {

        HorizontalDivider(
            color = customColors.borderLight,
            thickness = 1.dp
        )

        AnimatedVisibility(
            visible = replyingToUsername != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(customColors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.replying_to_label, "@$cachedUsername"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = customColors.textSecondary
                    )
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.cancel_reply_content_desc),
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { onCancelReply() },
                    tint = customColors.textSecondary
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(customColors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
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

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = customColors.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = customColors.borderLight,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = dynamicPlaceholder,
                        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textTertiary)
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = customColors.textPrimary),
                    cursorBrush = SolidColor(customColors.gourmetOrange),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                    color = customColors.gourmetOrange,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = stringResource(Res.string.profile_share),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (text.isNotBlank()) customColors.gourmetOrange else customColors.textTertiary
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = text.isNotBlank()) { onSendClick() }
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        }
    }
}