package org.beem.tastymap.ui.tastyview

expect class TastyDivider(
    modifier: TastyModifier = TastyModifier()
        .padding(top = 14, bottom = 14),
    color: String,
    thickness: Int = 1,
) : TastyView {
    override fun render(): TastyPlatformView
}