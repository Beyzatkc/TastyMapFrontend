package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalAlignment
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalArrangement


expect class TastyColumn(
    modifier: TastyModifier = TastyModifier(),
    verticalArrangement: TastyVerticalArrangement = TastyVerticalArrangement.Top,
    horizontalAlignment: TastyHorizontalAlignment = TastyHorizontalAlignment.Start,
    scrollable: Boolean = false,
    children: List<TastyView>
) : TastyView {
    override fun render(): TastyPlatformView
}