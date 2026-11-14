package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.beem.tastymap.ui.theme.CustomColors
import org.beem.tastymap.ui.theme.getAppFontFamily


@Composable
fun UserMessageBubble(
    text: String,
    customColors: CustomColors,
    modifier: Modifier = Modifier
) {
    val fontFamily = getAppFontFamily()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            color = customColors.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 6.dp
            ),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            SelectionContainer {
                Text(
                    text = text,
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = customColors.textPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                )
            }
        }
    }
}