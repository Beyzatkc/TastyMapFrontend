package org.beem.tastymap.ui.tastyview



expect class TastyLazyColumn(
    key: String,
    items: List<TastyView>,
    onLoadMore: () -> Unit
) : TastyView {
    override fun render(): Any
}