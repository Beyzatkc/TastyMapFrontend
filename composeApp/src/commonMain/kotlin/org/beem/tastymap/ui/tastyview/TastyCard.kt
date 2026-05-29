package org.beem.tastymap.ui.tastyview

expect class TastyCard(
    backgroundColor: String,
    cornerRadius: Int = 12,
    padding: Int = 12,
    children: List<TastyView>
) : TastyView {
    override fun render(): Any
}