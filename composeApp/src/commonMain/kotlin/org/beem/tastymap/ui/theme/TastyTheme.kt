package org.beem.tastymap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 1. ÖZEL RENK PALETİ TANIMI (Eklenen renkler dahil edildi)
data class CustomColors(
    val navy: Color,
    val navyDark: Color,
    val navySoft: Color,
    val backgroundBlue: Color,
    val background: Color,
    val lineAlpha: Color,
    val wave: Color,
    val gray: Color,
    val surfaceVariant: Color,
    val accentEmerald: Color,
    val accentAmber: Color,
    val gourmetOrange: Color,
    val gold: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val borderLight: Color,
    val borderStrong: Color,
    val green: Color,
    val yellow: Color,
    val red: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

val LightCustomColors = CustomColors(
    navy = AppColors.NavyBlue,
    navyDark = AppColors.NavyDark,
    navySoft = AppColors.NavySoft,
    backgroundBlue = AppColors.BackBackgroundBlue,
    background = AppColors.Background,
    lineAlpha = AppColors.DarkGrayLines.copy(alpha = 0.22f),
    wave = AppColors.WaveColor,
    gray = AppColors.LightGray,
    surfaceVariant = AppColors.SurfaceVariant,
    accentEmerald = AppColors.EmeraldAccent,
    accentAmber = AppColors.WarmAmber,
    gourmetOrange = AppColors.GourmetOrange,
    gold = AppColors.Gold,
    textPrimary = AppColors.TextPrimary,
    textSecondary = AppColors.TextSecondary,
    textTertiary = AppColors.TextTertiary,
    borderLight = AppColors.BorderLight,
    borderStrong = AppColors.BorderStrong,
    green = AppColors.passwordGreen,
    yellow = AppColors.passwordYellow,
    red = AppColors.passwordRed,
    success = AppColors.SuccessGreen,
    warning = AppColors.WarningYellow,
    error = AppColors.ErrorRed
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

// 2. MATERIAL3 SCHEME TANIMLARI (AppColors ile tam uyumlu)
private val LightColors = lightColorScheme(
    primary = AppColors.NavyBlue,
    onPrimary = Color.White,
    secondary = AppColors.EmeraldAccent,
    onSecondary = Color.White,
    tertiary = AppColors.GourmetOrange,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    outline = AppColors.BorderLight,
    error = AppColors.ErrorRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = AppColors.NavySoft,
    onPrimary = Color.White,
    secondary = AppColors.EmeraldAccent,
    background = Color(0xFF121417),
    surface = Color(0xFF1E293B),
    onSurface = Color.White
)

private val TastyShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(24.dp)
)

// 3. TASTY THEME (CompositionLocalProvider ile entegre)
@Composable
fun TastyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    val customColors = LightCustomColors

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = getAppTypography(),
            shapes = TastyShapes,
            content = content
        )
    }
}