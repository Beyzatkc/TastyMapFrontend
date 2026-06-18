package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon

actual class TastyIcon actual constructor(
    private val icon: TastyMapIcon,
    private val color: String,
    private val sizePx: Int
) : TastyView {

    actual override fun render(): TastyPlatformView {
        val finalSize = sizePx

        val modifierCss = modifier.toCssStyle()

        val svgHtml = icon.getHtmlIcon(color)

        val html = """
            <div class="tasty-icon" style="
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: ${finalSize}px;
                height: ${finalSize}px;
                flex-shrink: 0;
                $modifierCss
            ">
                $svgHtml
            </div>
        """.trimIndent()

        return html
    }
}