package org.beem.tastymap.ui.tastyview

actual class TastySpacer actual constructor(
    private val sizePx: Int
) : TastyView {

    actual override fun render(): Any {
        return """
            <div class="tasty-spacer" style="
                width: ${sizePx}px; 
                height: ${sizePx}px; 
                flex-shrink: 0;
                display: block;
            "></div>
        """.trimIndent()
    }
}