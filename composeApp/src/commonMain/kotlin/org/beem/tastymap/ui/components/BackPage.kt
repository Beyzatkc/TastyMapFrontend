package org.beem.tastymap.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.LocalCustomColors
import org.jetbrains.compose.resources.stringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.back_page_content_description

@Composable
fun BackPage(
    header: String,
    onBackClick: () -> Unit,
) {
    val colors = LocalCustomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(Res.string.back_page_content_description),
                tint = colors.textPrimary
            )
        }

        Text(
            text = header,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = colors.textPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}