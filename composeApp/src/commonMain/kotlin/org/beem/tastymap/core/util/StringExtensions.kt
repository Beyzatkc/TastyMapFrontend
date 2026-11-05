package org.beem.tastymap.core.util

import org.beem.tastymap.place.model.details.HighlightUiModel
import org.beem.tastymap.review.model.ScoreType

fun String.toBusinessStatusText(): String {
    return when (this.uppercase()) {
        "OPERATIONAL" -> "Açık"
        "CLOSED_TEMPORARILY" -> "Geçici Olarak Kapalı"
        "CLOSED_PERMANENTLY" -> "Kalıcı Olarak Kapalı"
        else -> if (this.isNotBlank()) this else "Bilinmiyor"
    }
}



fun parseHighlight(rawText: String): HighlightUiModel? {
    // "INTERIOR_DESIGN Öne Çıkıyor" -> split(" ", limit = 2)
    val parts = rawText.trim().split(" ", limit = 2)
    if (parts.isEmpty()) return null

    val rawEnum = parts[0]
    val suffix = parts.getOrNull(1) ?: "Öne Çıkıyor"

    val scoreType = runCatching { ScoreType.valueOf(rawEnum) }.getOrNull()
        ?: return null

    return HighlightUiModel(
        type = scoreType,
        title = scoreType.displayName,
        subtitle = suffix,
        content = "${scoreType.displayName} $suffix"
    )
}

fun List<String>.toHighlightUiModels(): List<HighlightUiModel> {
    return this.mapNotNull { parseHighlight(it) }
}