package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyHorizontalArrangement
import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyVerticalAlignment

expect class TastyRow(
    modifier: TastyModifier = TastyModifier(),
    horizontalArrangement: TastyHorizontalArrangement = TastyHorizontalArrangement.Start,
    verticalAlignment: TastyVerticalAlignment = TastyVerticalAlignment.Top,
    children: List<TastyView>
) : TastyView {
    override fun render(): TastyPlatformView
}