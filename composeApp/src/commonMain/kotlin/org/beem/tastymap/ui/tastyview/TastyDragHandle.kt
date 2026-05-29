package org.beem.tastymap.ui.tastyview

expect class TastyDragHandle(
    barColor: String
) : TastyView {
    override fun render(): Any
}