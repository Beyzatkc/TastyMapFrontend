package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.beem.tastymap.data.model.ai.AiProvider
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors

@Composable
fun AiProviderSelector(
    selectedProvider: AiProvider,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val currentIcon = if (selectedProvider == AiProvider.GROQ) Icons.Default.Bolt else Icons.Default.Psychology
    val currentAccent = if (selectedProvider == AiProvider.GROQ) Color(0xFFFF9800) else Color(0xFF7C4DFF)

    Box(modifier = modifier) {
        // Üst Bar ile Tamamen Bütünleşik Başlık Alanı
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                // Menü açıkken hafif bir zemin verir, kapalıyken tamamen şeffaf ve çerçevenizdir
                .background(
                    if (expanded) customColors.textPrimary.copy(alpha = 0.08f)
                    else Color.Transparent
                )
                .clickable(
                    onClick = { expanded = !expanded }
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = selectedProvider.brandPrefix,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = customColors.textPrimary
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = selectedProvider.modelTag,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = customColors.textSecondary.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Model Değiştir",
                tint = customColors.textSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }

        // Açılan Premium Kart Popup'ı
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(x = 0, y = 110),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Surface(
                    modifier = Modifier
                        .width(260.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.20f),
                                        Color.White.copy(alpha = 0.04f)
                                    )
                                )
                            ),
                            RoundedCornerShape(18.dp)
                        ),
                    color = customColors.darkHeaderColor.copy(alpha = 0.98f),
                    shadowElevation = 18.dp,
                    tonalElevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Yapay Zeka Motoru",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )

                        ProviderCardItem(
                            title = "${AiProvider.GROQ.brandPrefix} ${AiProvider.GROQ.modelTag}",
                            subtitle = "${AiProvider.GROQ.technicalName} · ${AiProvider.GROQ.description}",
                            badge = AiProvider.GROQ.badge,
                            accentColor = Color(0xFFFF9800),
                            icon = Icons.Default.Bolt,
                            isSelected = selectedProvider == AiProvider.GROQ,
                            onClick = {
                                onIntent(AiChatIntent.SelectProvider(AiProvider.GROQ))
                                expanded = false
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        ProviderCardItem(
                            title = "${AiProvider.EVREN.brandPrefix} ${AiProvider.EVREN.modelTag}", // "TastyAI Evren"
                            subtitle = "${AiProvider.EVREN.technicalName} · ${AiProvider.EVREN.description}",
                            badge = AiProvider.EVREN.badge,
                            accentColor = Color(0xFF7C4DFF),
                            icon = Icons.Default.Psychology,
                            isSelected = selectedProvider == AiProvider.EVREN,
                            onClick = {
                                onIntent(AiChatIntent.SelectProvider(AiProvider.EVREN))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderCardItem(
    title: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgModifier = if (isSelected) {
        Modifier
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
    } else {
        Modifier.background(Color.Transparent)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(bgModifier)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                }
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}