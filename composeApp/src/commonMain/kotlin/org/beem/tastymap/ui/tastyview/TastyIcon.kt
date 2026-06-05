package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.icons.TastyMapIcons

expect class TastyIcon(
    icon: TastyMapIcons,
    color: String,
    sizePx: Int = 18
) : TastyView {
    override fun render(): TastyPlatformView
}