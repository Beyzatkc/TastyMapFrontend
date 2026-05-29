package org.beem.tastymap.ui.tastyview

data class TastyModifier(
    val padding: Int = 0,
    val paddingBottom: Int = 0,
    val marginTop: Int = 0,
    val marginBottom: Int = 0,
    val fillMaxWidth: Boolean = false,
    val backgroundColor: String? = null,
    val width: String? = null,
    val height: String? = null,
    val borderRadius: Int = 0,
    val display: String = "flex",
    val flexDirection: String = "column",
    val justifyContent: String? = null,
    val alignItems: String? = null,
    val gap: Int = 0,
    val cursor: String? = null
) {
    fun padding(value: Int) = copy(padding = value)
    fun paddingBottom(value: Int) = copy(paddingBottom = value)
    fun marginTop(value: Int) = copy(marginTop = value)
    fun marginBottom(value: Int) = copy(marginBottom = value)
    fun fillMaxWidth() = copy(fillMaxWidth = true)
    fun background(color: String) = copy(backgroundColor = color)
    fun size(w: String, h: String) = copy(width = w, height = h)
    fun borderRadius(radius: Int) = copy(borderRadius = radius)
    fun flexProperties(dir: String = "column", justify: String? = null, align: String? = null, g: Int = 0) =
        copy(flexDirection = dir, justifyContent = justify, alignItems = align, gap = g)
    fun cursor(type: String) = copy(cursor = type)
}