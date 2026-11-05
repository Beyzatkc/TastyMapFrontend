package org.beem.tastymap.ui.components.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.beem.tastymap.ui.theme.AppColors

enum class TastyDialogType(
    val primaryColor: Color,
    val defaultIcon: ImageVector
) {
    DANGER(
        primaryColor = AppColors.ErrorRed,
        defaultIcon = Icons.Rounded.DeleteOutline
    ),
    WARNING(
        primaryColor = AppColors.WarmAmber,
        defaultIcon = Icons.Rounded.WarningAmber
    ),
    INFO(
        primaryColor = AppColors.NavySoft,
        defaultIcon = Icons.Rounded.Info
    ),
    SUCCESS(
        primaryColor = AppColors.SuccessGreen,
        defaultIcon = Icons.Rounded.CheckCircle
    )
}