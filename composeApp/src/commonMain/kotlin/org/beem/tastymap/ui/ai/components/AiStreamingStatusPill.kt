package org.beem.tastymap.ui.ai.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiStreamingStatusPill(
    statusMessage: String?,
    customColors: CustomColors,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = statusMessage != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        statusMessage?.let { status ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = customColors.surfaceVariant.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = customColors.gourmetOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = customColors.textPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}