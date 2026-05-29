package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.icons.TastyMapIcons

actual class TastyIcon actual constructor(
    private val icon: TastyMapIcons,
    private val color: String,
    private val sizePx: Int
) : TastyView {

    actual override fun render(): Any {
        val rawIconHtml = icon.getHtmlIcon()

        return """
            <div class="tasty-icon" style="
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: ${sizePx}px;
                height: ${sizePx}px;
                color: $color;
                fill: $color;
                flex-shrink: 0;
                vertical-align: middle; 
                position: relative;
                top: -1px; 
            ">
                <style>
                    .tasty-icon > div {
                        margin-top: 0px !important; 
                        width: 100% !important;
                        height: 100% !important;
                    }
                    .tasty-icon svg { 
                        width: 100%; 
                        height: 100%; 
                        display: block;
                    }
                </style>
                $rawIconHtml
            </div>
        """.trimIndent()
    }
}