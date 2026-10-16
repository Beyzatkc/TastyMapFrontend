package org.beem.tastymap.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.plusjakartasans_bold
import tastymap.composeapp.generated.resources.plusjakartasans_medium
import tastymap.composeapp.generated.resources.plusjakartasans_regular
import tastymap.composeapp.generated.resources.plusjakartasans_semibold


@Composable
fun getAppFontFamily() = FontFamily(
    Font(Res.font.plusjakartasans_regular, FontWeight.Normal),
    Font(Res.font.plusjakartasans_medium, FontWeight.Medium),
    Font(Res.font.plusjakartasans_semibold, FontWeight.SemiBold),
    Font(Res.font.plusjakartasans_bold, FontWeight.Bold)
)

@Composable
fun getAppTypography(): Typography {
    val fontFamily = getAppFontFamily()
    return Typography(
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AppColors.TextPrimary
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = AppColors.TextPrimary
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = AppColors.TextSecondary
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = AppColors.TextTertiary
        )
    )
}