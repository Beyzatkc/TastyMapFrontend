package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.contractual.TastyStickyScrollableContent
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement
import org.beem.tastymap.ui.tastyview.to.toCssAlignItems
import org.beem.tastymap.ui.tastyview.to.toCssJustifyContent

actual class TastyColumn actual constructor(
    override val modifier: TastyModifier,
    private val verticalArrangement: TastyVerticalArrangement,
    private val horizontalAlignment: TastyHorizontalAlignment,
    private val scrollable: Boolean,
    val children: List<TastyView>
) : TastyView, TastyStickyScrollableContent {
    actual override fun render(): TastyPlatformView {
        val childrenHtml = children.joinToString("") { child ->
            val childHtml = child.render()
            val weight = child.modifier.weight

            if (weight != null && weight > 0f) {
                """<div style="flex-grow: ${weight.toInt()}; flex-shrink: 1; flex-basis: 0px; height: 100%; display: flex; flex-direction: column;">$childHtml</div>"""
            } else {
                childHtml
            }
        }

        val justify = verticalArrangement.toCssJustifyContent()
        val align = horizontalAlignment.toCssAlignItems()

        val containerClass = if (scrollable){
            injectScrollbarStyleOnce()
            "no-scrollbar"
        } else ""

        val scrollStyle = if (scrollable) {
            """
            flex-grow: 1; 
            min-height: 0; 
            overflow-y: auto; 
            overflow-x: hidden;
            width: 100%;
            box-sizing: border-box;
            """.trimIndent()
        } else {
            "overflow: visible; width: 100%; box-sizing: border-box;"
        }

        val html = """
            <div class="$containerClass" style="display: flex; flex-direction: column; $scrollStyle $justify $align ${modifier.toCssStyle()}">
                $childrenHtml
            </div>
        """.trimIndent()


        return html
    }
}