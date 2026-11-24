package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.beem.tastymap.LocalIsWideScreen
import org.beem.tastymap.ui.theme.getAppFontFamily

private data class QuickPrompt(
    val label: String,
    val prompt: String,
    val icon: ImageVector
)

@Composable
fun AiWelcomeView(
    customColors: CustomColors = LocalCustomColors.current,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWideScreen = LocalIsWideScreen.current
    val fontFamily = getAppFontFamily()

    val quickPrompts = remember {
        listOf(
            QuickPrompt(
                label = "Nezih akşam yemeği",
                prompt = "Bu civarda nezih bir akşam yemeği için nereyi önerirsin?",
                icon = Icons.Rounded.Restaurant
            ),
            QuickPrompt(
                label = "Ders çalışmalık kafe",
                prompt = "Sakin, ders çalışmaya ve filtre kahve içmeye uygun bir kafe arıyorum.",
                icon = Icons.Rounded.Coffee
            ),
            QuickPrompt(
                label = "Yöresel lezzetler",
                prompt = "Bulunduğum bölgedeki en iyi yöresel lezzetleri nerede yiyebilirim?",
                icon = Icons.Rounded.LocalFireDepartment
            ),
            QuickPrompt(
                label = "Hafif tatlı molası",
                prompt = "Akşam için hafif bir tatlı ve çay molası verebileceğim bir mekan var mı?",
                icon = Icons.Rounded.BakeryDining
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Editoryal Başlık Alanı (Sola yaslı, klişe ikonsuz)
            Text(
                text = "Bugün nereyi keşfedelim?",
                fontFamily = fontFamily,
                fontSize = if (isWideScreen) 28.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                lineHeight = if (isWideScreen) 34.sp else 30.sp,
                color = customColors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Canlı puan ve saatlerle sana en uygun mekanı bul.",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = customColors.textSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2x2 Kompakt Öneri Izgarası (Mobil & Web tutarlı)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quickPrompts.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            QuickPromptTile(
                                item = item,
                                customColors = customColors,
                                onClick = { onIntent(AiChatIntent.InputTextChanged(item.prompt)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPromptTile(
    item: QuickPrompt,
    customColors: CustomColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = customColors.surface,
        border = BorderStroke(1.dp, customColors.borderLight.copy(alpha = 0.7f)),
        modifier = modifier
            .heightIn(min = 96.dp)
            .pointerHoverIcon(PointerIcon.Hand)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = customColors.gourmetOrange,
                    modifier = Modifier.size(20.dp)
                )

                Icon(
                    imageVector = Icons.Rounded.NorthEast,
                    contentDescription = null,
                    tint = customColors.textTertiary.copy(alpha = 0.75f),
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = item.label,
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp,
                color = customColors.textPrimary,
                maxLines = 2
            )
        }
    }
}