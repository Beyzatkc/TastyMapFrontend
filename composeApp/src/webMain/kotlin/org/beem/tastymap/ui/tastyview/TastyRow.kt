package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment
import org.beem.tastymap.ui.tastyview.to.toCssAlignItems
import org.beem.tastymap.ui.tastyview.to.toCssJustifyContent

actual class TastyRow actual constructor(
    override val modifier: TastyModifier,
    private val horizontalArrangement: TastyHorizontalArrangement,
    private val verticalAlignment: TastyVerticalAlignment,
    private val children: List<TastyView>
) : TastyView {
    actual override fun render(): TastyPlatformView {
        val childrenHtml = children.joinToString("") { child ->
            val childHtml = child.render()
            val weight = child.modifier.weight

            if (weight != null && weight > 0f) {
                """<div style="flex-grow: ${weight.toInt()}; flex-shrink: 1; flex-basis: 0px; width: 100%; display: flex;">$childHtml</div>"""
            } else {
                childHtml
            }
        }

        val justify = horizontalArrangement.toCssJustifyContent()
        val align = verticalAlignment.toCssAlignItems()

        val html = """
            <div style="display: flex; flex-direction: row; $justify $align ${modifier.toCssStyle()}">
                $childrenHtml
            </div>
        """.trimIndent()

        return html
    }
}