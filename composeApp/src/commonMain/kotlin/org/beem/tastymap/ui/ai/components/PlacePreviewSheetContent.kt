package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun PlacePreviewSheet(
    place: HydratedPlaceCardDto,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = customColors.darkHeaderColor,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Küçük sürükleme çizgisi
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    place.vicinity?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (place.googleRating != null && place.googleRating > 0.0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFB800).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.googleRating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = { onIntent(AiChatIntent.DismissPlacePreview) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    place.dbId?.let { id -> onIntent(AiChatIntent.PlaceClicked(id)) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = customColors.gourmetOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text("Mekanı İncele", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}