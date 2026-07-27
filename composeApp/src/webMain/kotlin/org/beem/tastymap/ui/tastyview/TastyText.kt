package org.beem.tastymap.ui.tastyview

actual class TastyText actual constructor(
    private val text: String,
    private val style: TastyTextStyle,
    private val color: String?,
    private val maxLines: Int,
    private val onOverflow: (() -> Unit)?,
    override val modifier: TastyModifier
) : TastyView {

    actual override fun render(): TastyPlatformView {
        val textColorStyle = if (color != null) "color: $color;" else ""

        val modifierCss = modifier.toCssStyle()

        val html = when (style) {
            TastyTextStyle.TITLE -> """
                <h2 style="margin: 0; font-size: 1.45rem; font-weight: 800; letter-spacing: -0.3px; line-height: 1.2; $textColorStyle $modifierCss">
                    $text
                </h2>
            """.trimIndent()

            TastyTextStyle.SUBTITLE -> """
                <h3 style="margin: 0; font-size: 1rem; font-weight: 700; $textColorStyle $modifierCss">
                    $text
                </h3>
            """.trimIndent()

            TastyTextStyle.BODY -> """
                <p style="margin: 0; font-size: 0.88rem; line-height: 1.4; font-weight: 400; $textColorStyle $modifierCss">
                    $text
                </p>
            """.trimIndent()

            TastyTextStyle.BADGE -> """
                <span style="display: inline-block; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; $textColorStyle $modifierCss">
                    $text
                </span>
            """.trimIndent()
        }

        return html
    }
}