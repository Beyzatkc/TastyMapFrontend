package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.AiChatIntent
import org.beem.tastymap.ui.theme.CustomColors

private const val TAG_PLACE_CLICK = "PLACE_CLICK"

@Composable
fun AiFormattedMessageText(
    rawText: String,
    places: List<HydratedPlaceCardDto>,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Kaçış karakterlerini temizle
    val cleanText = remember(rawText) {
        rawText.replace("\\n", "\n").replace("\\\"", "\"")
    }

    val annotatedString = remember(cleanText, places) {
        buildAnnotatedString {
            val regex = Regex("\\*\\*(.*?)\\*\\*")
            var currentIndex = 0

            regex.findAll(cleanText).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1
                val boldContent = matchResult.groupValues[1].trim()

                if (start > currentIndex) {
                    append(cleanText.substring(currentIndex, start))
                }

                // Vurgu listedeki bir mekanla eşleşiyor mu?
                val matchedPlace = places.firstOrNull { place ->
                    place.name.equals(boldContent, ignoreCase = true) ||
                            boldContent.contains(place.name, ignoreCase = true) ||
                            place.name.contains(boldContent, ignoreCase = true)
                }

                if (matchedPlace != null) {
                    // Tıklanabilir mekan
                    pushStringAnnotation(tag = TAG_PLACE_CLICK, annotation = matchedPlace.dbId?.toString() ?: matchedPlace.name)
                    withStyle(
                        style = SpanStyle(
                            color = customColors.gourmetOrange,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(boldContent)
                    }
                    pop()
                } else {
                    // Düz kalın vurgu
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = customColors.textPrimary
                        )
                    ) {
                        append(boldContent)
                    }
                }

                currentIndex = end
            }

            if (currentIndex < cleanText.length) {
                append(cleanText.substring(currentIndex))
            }
        }
    }

    var textLayoutResult: TextLayoutResult? = null

    BasicText(
        text = annotatedString,
        modifier = modifier.pointerInput(annotatedString) {
            detectTapGestures { offsetPosition ->
                textLayoutResult?.let { layoutResult ->
                    val offset = layoutResult.getOffsetForPosition(offsetPosition)
                    annotatedString.getStringAnnotations(TAG_PLACE_CLICK, offset, offset)
                        .firstOrNull()?.let { annotation ->
                            val matchedPlace = places.firstOrNull {
                                it.dbId?.toString() == annotation.item || it.name == annotation.item
                            }
                            if (matchedPlace != null) {
                                onIntent(AiChatIntent.ShowPlacePreview(matchedPlace))
                            }
                        }
                }
            }
        },
        style = MaterialTheme.typography.bodyMedium.copy(
            color = customColors.textPrimary,
            lineHeight = 22.sp
        ),
        onTextLayout = { textLayoutResult = it }
    )
}