package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.tastylayoutenums.TastyBoxAlignment

expect class TastyBox(
    modifier: TastyModifier = TastyModifier(),
    contentAlignment: TastyBoxAlignment = TastyBoxAlignment.TOP_START,
    children: List<TastyView>
) : TastyView {
    override fun render(): TastyPlatformView
}