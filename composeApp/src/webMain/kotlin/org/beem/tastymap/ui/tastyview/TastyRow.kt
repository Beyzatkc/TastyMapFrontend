package org.beem.tastymap.ui.tastyview

actual class TastyRow actual constructor(
    private val modifier: TastyModifier,
    private val children: List<TastyView>
) : TastyView {
    actual override fun render(): Any {
        val childrenHtml = children.joinToString("") { it.render() as String }
        return """<div style="display: flex; flex-direction: row; ${modifier.toCssStyle()}">$childrenHtml</div>"""
    }
}