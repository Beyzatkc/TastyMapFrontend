package org.beem.tastymap.ui.icons

import androidx.annotation.ColorLong

enum class TastyMapIcons(
    val iconName: String,
    val resPath: String,
    val sizeDp: Int,
    val color: String
) {
    LOCATION(
        iconName = "tm_location",
        resPath = "drawable/location.svg",
        sizeDp = 18,
        color = "#f54254"
    ),
    STAR(
        iconName = "tm_star",
        resPath = "drawable/star.svg",
        sizeDp = 18,
        color = "#FFD700"
    );


    fun getHtmlIcon(): String {
        val rawSvg = TastyMapIconsManager.getRawSvg(this)
        if (rawSvg.isEmpty()) return ""

        val processedSvg = rawSvg
            .replace(Regex("""(?<!-)stroke="[^"]*""""), """stroke="$color"""")
            .replace(Regex("""(?<!-)fill="(?!none\b)[^"]*""""), """fill="$color"""")
            .replace(Regex("""(?<!-)width="[^"]*""""), """width="100%"""")
            .replace(Regex("""(?<!-)height="[^"]*""""), """height="100%"""")

        return """
            <div style="width: ${sizeDp}px; height: ${sizeDp}px; margin-top: 3px; flex-shrink: 0; display: flex; align-items: center; justify-content: center;">
                $processedSvg
            </div>
        """.trimIndent()
    }
}