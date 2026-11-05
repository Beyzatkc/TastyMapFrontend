package org.beem.tastymap.ui.tastyview

expect class TastySpacer(
    modifier: TastyModifier = TastyModifier()
) : TastyView {
    override fun render(): TastyPlatformView
}