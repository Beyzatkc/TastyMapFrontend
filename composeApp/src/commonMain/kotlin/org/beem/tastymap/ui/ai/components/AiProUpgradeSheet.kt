package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.*
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
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiProUpgradeSheet(
    customColors: CustomColors,
    onDismiss: () -> Unit
) {
    val fontFamily = getAppFontFamily()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = customColors.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = customColors.borderStrong)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst "ÇOK YAKINDA" Rozeti
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(customColors.gourmetOrange.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = customColors.gourmetOrange,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "ÇOK YAKINDA",
                    fontFamily = fontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = customColors.gourmetOrange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TastyAI Pro",
                fontFamily = fontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gastronomi asistanınızı günlük limitlere takılmadan, en gelişmiş yapay zeka modelleriyle kullanın.",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                color = customColors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Özellik Listesi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(customColors.surfaceVariant)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProFeatureRow(
                    icon = Icons.Rounded.AllInclusive,
                    title = "10 Kat Daha Yüksek Günlük Kota",
                    subtitle = "Günlük 500.000 token ile kesintisiz mekan keşfi",
                    customColors = customColors
                )
                ProFeatureRow(
                    icon = Icons.Rounded.Psychology,
                    title = "Gelişmiş Akıl Yürütme Modelleri",
                    subtitle = "Çoklu mekan karşılaştırması ve detaylı menü analizi",
                    customColors = customColors
                )
                ProFeatureRow(
                    icon = Icons.Rounded.Bolt,
                    title = "Öncelikli Yanıt Hızı",
                    subtitle = "Yoğun saatlerde bile beklemeden anında streaming",
                    customColors = customColors
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColors.gourmetOrange,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Anladım",
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    customColors: CustomColors
) {
    val fontFamily = getAppFontFamily()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(customColors.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customColors.gourmetOrange,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = customColors.textPrimary
            )
            Text(
                text = subtitle,
                fontFamily = fontFamily,
                fontSize = 12.sp,
                color = customColors.textSecondary
            )
        }
    }
}