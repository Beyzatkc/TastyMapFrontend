package org.beem.tastymap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    profilePhotoUrl: String? = null,
    placeholderText: String = "Yorum ekle...", // Yanıtla durumunda "Yanıtla: @mikaraun" olarak değiştirilebilir
    isLoading: Boolean = false
) {
    val customColors = LocalCustomColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(customColors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = customColors.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (text.isEmpty()) {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textSecondary)
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
                    .padding(end = 8.dp),
                color = customColors.gourmetOrange,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = "Paylaş",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (text.isNotBlank()) customColors.gourmetOrange else customColors.textSecondary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .clickable(enabled = text.isNotBlank()) { onSendClick() }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }
    }
}