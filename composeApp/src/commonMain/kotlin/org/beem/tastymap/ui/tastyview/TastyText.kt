package org.beem.tastymap.ui.tastyview

expect class TastyText(
    text: String,
    style: TastyTextStyle = TastyTextStyle.BODY,
    color: String? = null,
    modifier: TastyModifier = TastyModifier()
) : TastyView {
    override fun render(): TastyPlatformView
}