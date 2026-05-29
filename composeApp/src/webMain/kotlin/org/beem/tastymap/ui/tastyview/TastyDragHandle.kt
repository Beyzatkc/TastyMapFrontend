package org.beem.tastymap.ui.tastyview

actual class TastyDragHandle actual constructor(
    private val barColor: String
) : TastyView {
    actual override fun render(): Any {
        return """
            <div id="sheet-drag-handle" style="width: 100%; height: 20px; display: flex; justify-content: center; align-items: center; cursor: grab; margin-bottom: 4px;">
                <div style="width: 44px; height: 5px; background: $barColor; border-radius: 100px;"></div>
            </div>
        """.trimIndent()
    }
}