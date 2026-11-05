package org.beem.tastymap.ui.tastyview

import org.beem.tastymap.ui.tastyview.icons.TastyMapIcon

expect class TastyIcon(
    icon: TastyMapIcon,
    color: String,
    sizePx: Int = 18
) : TastyView {
    override fun render(): TastyPlatformView
}