package org.beem.tastymap.ui.tastyview

expect class TastyText(
    text: String,
    style: TastyTextStyle = TastyTextStyle.BODY,
    color: String? = null
) : TastyView {
    override fun render(): Any
}