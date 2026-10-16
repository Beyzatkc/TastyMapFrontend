package org.beem.tastymap.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.beem.tastymap.ui.theme.LocalCustomColors

@Composable
fun MentionFormattedText(text: String) {
    val customColors = LocalCustomColors.current

    val annotatedString = buildAnnotatedString {
        val regex = Regex("(@\\w+)")
        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))

            withStyle(style = SpanStyle(color = customColors.gourmetOrange, fontWeight = FontWeight.Bold)) {
                append(matchResult.value)
            }
            lastIndex = matchResult.range.last + 1
        }
        append(text.substring(lastIndex))
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = customColors.textPrimary)
    )
}