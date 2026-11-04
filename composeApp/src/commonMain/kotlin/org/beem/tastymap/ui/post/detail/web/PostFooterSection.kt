package org.beem.tastymap.ui.post.detail.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.core.util.formatToRelativeDateTime
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.ui.components.CommentInputBar
import org.beem.tastymap.ui.post.detail.PostActionBar
import org.beem.tastymap.ui.theme.CustomColors


@Composable
fun PostFooterSection(
    post: PostResponse,
    customColors: CustomColors,
    commentText: String,
    replyingToUsername: String?,
    isSendingComment: Boolean,
    currentUserProfileUrl: String?,
    onLikeClick: () -> Unit,
    onLikeCountClick: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSendComment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(customColors.background)
    ) {
        HorizontalDivider(color = customColors.surfaceVariant.copy(alpha = 0.5f))

        PostActionBar(
            post = post,
            onLikeClick = onLikeClick,
            onLikeCountClick = onLikeCountClick,
            onCommentClick = { /* Odaklanmayı input box'a yönlendir */ }
        )

        Text(
            text = formatToRelativeDateTime(post.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = customColors.textSecondary.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = customColors.surfaceVariant,
            thickness = 1.2.dp
        )

        if (post.isCommentEnabled) {
            CommentInputBar(
                text = commentText,
                onTextChange = onCommentTextChange,
                replyingToUsername = replyingToUsername,
                onCancelReply = onCancelReply,
                onSendClick = onSendComment,
                isLoading = isSendingComment,
                profilePhotoUrl = currentUserProfileUrl
            )
        }
    }
}
