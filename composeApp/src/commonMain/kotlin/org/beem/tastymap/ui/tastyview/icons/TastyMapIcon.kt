package org.beem.tastymap.ui.tastyview.icons

enum class TastyMapIcon(
    val iconName: String,
    val resPath: String,
    val sizeDp: Int,
    val defaultColor: String
) {
    LOCATION(
        iconName = "tm_location",
        resPath = "drawable/location_web.svg",
        sizeDp = 18,
        defaultColor = "#f54254"
    ),
    STAR(
        iconName = "tm_star",
        resPath = "drawable/star_web.svg",
        sizeDp = 18,
        defaultColor = "#FFD700"
    );


    fun getHtmlIcon(finalColor: String = defaultColor): String {
        val rawSvg = TastyMapIconsManager.getRawSvg(this)
        if (rawSvg.isEmpty()) return ""

        return rawSvg
            .replace(Regex("""(?<!-)stroke="[^"]*""""), """stroke="$finalColor"""")
            .replace(Regex("""(?<!-)fill="(?!none\b)[^"]*""""), """fill="$finalColor"""")
            .replace(Regex("""(?<!-)width="[^"]*""""), """width="100%"""")
            .replace(Regex("""(?<!-)height="[^"]*""""), """height="100%"""")
    }
}