package org.beem.tastymap.ui.tastyview

class TastyModifier {
    var padding: Int = 0 ; private set
    var paddingBottom: Int = 0 ; private set
    var paddingTop: Int = 0 ; private set
    var paddingLeft: Int = 0 ; private set
    var paddingRight: Int = 0 ; private set

    var marginTop: Int = 0 ; private set
    var marginBottom: Int = 0 ; private set
    var fillMaxWidth: Boolean = false ; private set
    var backgroundColor: String? = null ; private set
    var widthPx: Int? = null ; private set
    var heightPx: Int? = null ; private set
    var borderRadius: Int = 0 ; private set

    var flexDirection: TastyFlexDirection = TastyFlexDirection.COLUMN ; private set
    var justifyContent: TastyJustifyContent = TastyJustifyContent.START ; private set
    var alignItems: TastyAlignItems = TastyAlignItems.START ; private set
    var gap: Int = 0 ; private set

    var weight: Float? = null ; private set


    fun padding(dp: Int) = apply { this.padding = dp }
    fun padding(top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0) = apply {
        this.paddingTop = top
        this.paddingRight = right
        this.paddingBottom = bottom
        this.paddingLeft = left
    }
    fun paddingBottom(dp: Int) = apply { this.paddingBottom = dp }
    fun marginTop(dp: Int) = apply { this.marginTop = dp }
    fun marginBottom(dp: Int) = apply { this.marginBottom = dp }
    fun fillMaxWidth() = apply { this.fillMaxWidth = true }
    fun background(color: String) = apply { this.backgroundColor = color }
    fun size(width: Int, height: Int) = apply {
        this.widthPx = width
        this.heightPx = height
    }
    fun width(width: Int) = apply { this.widthPx = width }
    fun height(height: Int) = apply { this.heightPx = height }
    fun borderRadius(dp: Int) = apply { this.borderRadius = dp }

    fun weight(value: Float) = apply { this.weight = value }

    fun layout(
        direction: TastyFlexDirection = TastyFlexDirection.COLUMN,
        justify: TastyJustifyContent = TastyJustifyContent.START,
        align: TastyAlignItems = TastyAlignItems.START,
        gapDp: Int = 0
    ) = apply {
        this.flexDirection = direction
        this.justifyContent = justify
        this.alignItems = align
        this.gap = gapDp
    }

    companion object {
        val Instance = TastyModifier()
    }
}