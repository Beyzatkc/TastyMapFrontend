package org.beem.tastymap.ui.tastyview

expect class TastySpacer(sizePx: Int) : TastyView {
    override fun render(): TastyPlatformView
}