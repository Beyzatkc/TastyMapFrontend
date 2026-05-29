package org.beem.tastymap.ui.tastyview


expect class TastyColumn(
    modifier: TastyModifier = TastyModifier(),
    children: List<TastyView>
) : TastyView {
    override fun render(): Any
}