package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AiProvider(
    val brandPrefix: String,
    val modelTag: String,
    val technicalName: String,
    val badge: String,
    val description: String
) {
    @SerialName("GROQ")
    GROQ(
        brandPrefix = "TastyAI",
        modelTag = "Hızlı",
        technicalName = "Qwen 27B (Groq)",
        badge = "Seri",
        description = "Anlık mekan ve lezzet önerileri"
    ),

    @SerialName("EVREN")
    EVREN(
        brandPrefix = "TastyAI",
        modelTag = "Evren",
        technicalName = "Qwen 30B VL (SSYZ)",
        badge = "Gelişmiş",
        description = "Derin analiz ve zengin bağlam"
    );

    companion object {
        fun fromString(value: String?): AiProvider {
            if (value.isNullOrBlank()) return GROQ
            return entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) } ?: GROQ
        }
    }
}