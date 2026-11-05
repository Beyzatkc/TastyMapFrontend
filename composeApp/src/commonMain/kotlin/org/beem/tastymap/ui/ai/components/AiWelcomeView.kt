package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors

private data class QuickPrompt(
    val title: String,
    val prompt: String,
    val icon: ImageVector
)

@Composable
fun AiWelcomeView(
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPrompts = listOf(
        QuickPrompt(
            title = "Nezih Akşam Yemeği",
            prompt = "Bu civarda nezih bir akşam yemeği için nereyi önerirsin?",
            icon = Icons.Default.Restaurant
        ),
        QuickPrompt(
            title = "Çalışma & Kahve",
            prompt = "Sakin, ders çalışmaya ve filtre kahve içmeye uygun bir kafe arıyorum.",
            icon = Icons.Default.Coffee
        ),
        QuickPrompt(
            title = "Yöresel Lezzetler",
            prompt = "En iyi fırın kebabı ve etliekmeği nerede yiyebilirim?",
            icon = Icons.Default.LocalFireDepartment
        ),
        QuickPrompt(
            title = "Hafif Tatlı Molası",
            prompt = "Akşam için hafif bir tatlı ve çay molası verebileceğim bir mekan var mı?",
            icon = Icons.Default.MenuBook
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // AI İkonu / Logo Rozeti
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(customColors.gold.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = customColors.gold,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Karşılama Başlığı
        Text(
            text = "Bugün ne keşfetmek istersin?",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = customColors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Mekan, lezzet veya ambiyans tarif et; sana en uygun noktaları canlı puan ve çalışma saatleriyle listeliyeyim.",
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textSecondary.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Hızlı Başlangıç Kartları (2x2 Grid benzeri Column)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            quickPrompts.forEach { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            // Tıklandığında input kutusunu bu metinle doldurur
                            onIntent(AiChatIntent.InputTextChanged(item.prompt))
                        },
                    color = customColors.surfaceVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(customColors.gourmetOrange.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = customColors.gourmetOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = customColors.textPrimary
                            )
                            Text(
                                text = item.prompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.textSecondary.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}