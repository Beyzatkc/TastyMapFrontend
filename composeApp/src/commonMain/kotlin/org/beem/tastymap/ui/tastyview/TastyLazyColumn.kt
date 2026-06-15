package org.beem.tastymap.ui.tastyview



expect class TastyLazyColumn(
    modifier: TastyModifier,
    key: String,
    items: List<TastyView>,
    onLoadMore: () -> Unit
) : TastyView{
    override fun render(): TastyPlatformView
}