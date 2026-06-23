package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.contractual.TastyStickyScrollableContent
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement


expect class TastyColumn(
    modifier: TastyModifier = TastyModifier(),
    verticalArrangement: TastyVerticalArrangement = TastyVerticalArrangement.Top,
    horizontalAlignment: TastyHorizontalAlignment = TastyHorizontalAlignment.Start,
    scrollable: Boolean = false,
    children: List<TastyView>
) : TastyView, TastyStickyScrollableContent {
    override fun render(): TastyPlatformView
}