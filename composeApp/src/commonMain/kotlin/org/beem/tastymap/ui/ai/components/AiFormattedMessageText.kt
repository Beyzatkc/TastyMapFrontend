package org.beem.tastymap.ui.ai.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
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
import org.beem.tastymap.ui.theme.adaptiveTextPointer
import org.beem.tastymap.ui.theme.getAppFontFamily

private const val TAG_PLACE_CLICK = "PLACE_CLICK"

@Composable
fun AiFormattedMessageText(
    rawText: String,
    places: List<HydratedPlaceCardDto>,
    customColors: CustomColors,
    onIntent: (AiChatIntent) -> Unit,
    modifier: Modifier = Modifier,
    isStreaming: Boolean = false
) {
    val fontFamily = getAppFontFamily()

    val cleanText = remember(rawText) {
        rawText
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
    }

    val annotatedString = remember(cleanText, places, isStreaming, customColors) {
        buildEditorialAnnotatedString(
            text = cleanText,
            places = places,
            customColors = customColors,
            isStreaming = isStreaming
        )
    }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    SelectionContainer(
        modifier = Modifier.adaptiveTextPointer()
    ) {
        Text(
            text = annotatedString,
            fontFamily = fontFamily,
            fontSize = 15.5.sp,
            lineHeight = 25.sp,
            letterSpacing = 0.15.sp,
            color = customColors.textPrimary,
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
            onTextLayout = { textLayoutResult = it }
        )
    }
}

private fun buildEditorialAnnotatedString(
    text: String,
    places: List<HydratedPlaceCardDto>,
    customColors: CustomColors,
    isStreaming: Boolean
): AnnotatedString = buildAnnotatedString {
    val lines = text.lines()

    lines.forEachIndexed { lineIndex, rawLine ->
        val trimmed = rawLine.trimStart()

        when {
            // ### Başlık satırları
            trimmed.startsWith("### ") -> {
                withStyle(
                    SpanStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    )
                ) {
                    appendInlineBoldAndPlaces(trimmed.removePrefix("### "), places, customColors)
                }
            }
            // ## Ana Başlık satırları
            trimmed.startsWith("## ") -> {
                withStyle(
                    SpanStyle(
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = customColors.textPrimary
                    )
                ) {
                    appendInlineBoldAndPlaces(trimmed.removePrefix("## "), places, customColors)
                }
            }
            // Madde işaretleri (* veya - )
            trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                withStyle(
                    SpanStyle(
                        color = customColors.gourmetOrange,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("  •  ")
                }
                appendInlineBoldAndPlaces(trimmed.drop(2), places, customColors)
            }
            else -> {
                appendInlineBoldAndPlaces(rawLine, places, customColors)
            }
        }

        if (lineIndex < lines.lastIndex) {
            append("\n")
        }
    }

    if (isStreaming) {
        withStyle(
            SpanStyle(
                color = customColors.gourmetOrange,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(" ▍")
        }
    }
}

private fun AnnotatedString.Builder.appendInlineBoldAndPlaces(
    segment: String,
    places: List<HydratedPlaceCardDto>,
    customColors: CustomColors
) {
    val regex = Regex("\\*\\*(.*?)\\*\\*")
    var currentIndex = 0

    regex.findAll(segment).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1
        val boldContent = matchResult.groupValues[1].trim()

        if (start > currentIndex) {
            append(segment.substring(currentIndex, start))
        }

        val matchedPlace = places.firstOrNull { place ->
            place.name.equals(boldContent, ignoreCase = true) ||
                    boldContent.contains(place.name, ignoreCase = true) ||
                    place.name.contains(boldContent, ignoreCase = true)
        }

        if (matchedPlace != null) {
            pushStringAnnotation(
                tag = TAG_PLACE_CLICK,
                annotation = matchedPlace.dbId?.toString() ?: matchedPlace.name
            )
            withStyle(
                SpanStyle(
                    color = customColors.gourmetOrange,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(boldContent)
            }
            pop()
        } else {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = customColors.textPrimary
                )
            ) {
                append(boldContent)
            }
        }

        currentIndex = end
    }

    if (currentIndex < segment.length) {
        append(segment.substring(currentIndex))
    }
}