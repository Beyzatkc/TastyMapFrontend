package org.beem.tastymap.ui.tastyview

actual class TastyDivider actual constructor(
    private val color: String,
    private val thicknessPx: Int,
    private val marginTop: Int,
    private val marginBottom: Int
) : TastyView {
    actual override fun render(): Any {
        return """
            <div style="width: 100%; height: ${thicknessPx}px; background-color: $color; margin-top: ${marginTop}px; margin-bottom: ${marginBottom}px;"></div>
        """.trimIndent()
    }
}