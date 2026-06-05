package org.beem.tastymap.ui.tastyview

actual class TastyColumn actual constructor(
    private val modifier: TastyModifier,
    private val children: List<TastyView>
) : TastyView {
    actual override fun render(): TastyPlatformView {
        val childrenHtml = children.joinToString("") { it.render() as String }
        return """<div style="display: flex; flex-direction: column; ${modifier.toCssStyle()}">$childrenHtml</div>"""
    }
}