package org.beem.tastymap.ui.tastyview

expect class TastyDivider(
    color: String,
    thicknessPx: Int = 1,
    marginTop: Int = 14,
    marginBottom: Int = 14
) : TastyView {
    override fun render(): Any
}