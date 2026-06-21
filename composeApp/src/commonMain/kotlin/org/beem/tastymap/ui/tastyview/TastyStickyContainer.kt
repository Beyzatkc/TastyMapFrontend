package org.beem.tastymap.ui.tastyview

expect class TastyStickyContainer(
    modifier: TastyModifier,
    stickyHeader: TastyView,
    scrollableContent: TastyView
): TastyView {
    override fun render(): TastyPlatformView
}