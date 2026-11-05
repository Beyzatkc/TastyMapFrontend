package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyBoxAlignment
import org.beem.tastymap.ui.tastyview.to.toCssAlignItems

actual class TastyBox actual constructor(
    override val modifier: TastyModifier,
    private val contentAlignment: TastyBoxAlignment,
    private val children: List<TastyView>
): TastyView{

    actual override fun render(): TastyPlatformView {
        val childrenHtml = children.joinToString("") { child ->
            child.render()
        }

        val alignmentCss = contentAlignment.toCssAlignItems()

        val html = """
            <div style="display: flex; position: relative; box-sizing: border-box; $alignmentCss ${modifier.toCssStyle()}">
                $childrenHtml
            </div>
        """.trimIndent()

        return html
    }
}