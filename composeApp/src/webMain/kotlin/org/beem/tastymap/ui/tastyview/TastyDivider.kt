package org.beem.tastymap.ui.tastyview

actual class TastyDivider actual constructor(
    override val modifier: TastyModifier,
    private val color: String,
    private val thickness: Int,
) : TastyView {

    actual override fun render(): TastyPlatformView {
        val modifierCss = modifier.toCssStyle()

        val html = """
            <div style="width: 100%; height: ${thickness}px; background-color: $color; flex-shrink: 0; $modifierCss"></div>
        """.trimIndent()

        return html
    }
}