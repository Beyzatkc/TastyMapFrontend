package org.beem.tastymap.ui.components


import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt

@Composable
actual fun TastyMapFab(
    onClick: () -> Unit,
    modifier: Modifier,
    backgroundColor: String,
    iconColor: String,
    zIndex: Float,
    icon: TastyIcon
) {
    val bgColor = Color(backgroundColor.toColorInt())
    val contentColor = Color(iconColor.toColorInt())

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.zIndex(zIndex),
        containerColor = bgColor,
        contentColor = contentColor,
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon.getMobileIcon(),
            contentDescription = null
        )
    }
}