package org.beem.tastymap.ui.tastyview

fun TastyModifier.toCssStyle(): String {
    val styles = mutableListOf<String>()

    if (this.fillMaxWidth) styles.add("width: 100%")
    this.widthPx?.let { styles.add("width: ${it}px") }
    this.heightPx?.let { styles.add("height: ${it}px") }

    if (this.marginTop > 0) styles.add("margin-top: ${this.marginTop}px")
    if (this.marginBottom > 0) styles.add("margin-bottom: ${this.marginBottom}px")

    val finalTop = if (this.paddingTop > 0) this.paddingTop else this.padding
    val finalBottom = if (this.paddingBottom > 0) this.paddingBottom else this.padding
    val finalLeft = if (this.paddingLeft > 0) this.paddingLeft else this.padding
    val finalRight = if (this.paddingRight > 0) this.paddingRight else this.padding

    if (finalTop > 0) styles.add("padding-top: ${finalTop}px")
    if (finalBottom > 0) styles.add("padding-bottom: ${finalBottom}px")
    if (finalLeft > 0) styles.add("padding-left: ${finalLeft}px")
    if (finalRight > 0) styles.add("padding-right: ${finalRight}px")

    this.backgroundColor?.let { styles.add("background-color: $it") }
    if (this.borderRadius > 0) styles.add("border-radius: ${this.borderRadius}px")

    if (this.gap > 0) styles.add("gap: ${this.gap}px")

    return styles.joinToString("; ")
}