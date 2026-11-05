package org.beem.tastymap.ui.tastyview

actual class TastySpacer actual constructor(
   override val modifier: TastyModifier
) : TastyView {

    actual override fun render(): TastyPlatformView {
        val html = """
            <div class="tasty-spacer" style="
                flex-shrink: 0;
                display: block;
                ${modifier.toCssStyle()}
            "></div>
        """.trimIndent()

        return html
    }
}