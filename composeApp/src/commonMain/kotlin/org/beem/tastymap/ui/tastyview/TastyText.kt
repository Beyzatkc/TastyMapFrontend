package org.beem.tastymap.ui.tastyview

expect class TastyText(
    text: String,
    style: TastyTextStyle = TastyTextStyle.BODY,
    color: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    onOverflow: (() -> Unit)? = null,
    modifier: TastyModifier = TastyModifier()
) : TastyView {
    override fun render(): TastyPlatformView
}