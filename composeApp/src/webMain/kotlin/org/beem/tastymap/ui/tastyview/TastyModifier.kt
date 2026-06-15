package org.beem.tastymap.ui.tastyview


fun TastyModifier.toCssStyle(): String {
    val styles = mutableListOf<String>()

    styles.add("display: flex")
    styles.add("flex-direction: ${this.flexDirection.name.lowercase()}")

    if (this.fillMaxWidth) styles.add("width: 100%")
    this.widthPx?.let { styles.add("width: ${it}px") }
    this.heightPx?.let { styles.add("height: ${it}px") }

    if (this.padding > 0) styles.add("padding: ${this.padding}px")
    if (this.paddingBottom > 0) styles.add("padding-bottom: ${this.paddingBottom}px")
    if (this.marginTop > 0) styles.add("margin-top: ${this.marginTop}px")
    if (this.marginBottom > 0) styles.add("margin-bottom: ${this.marginBottom}px")

    this.backgroundColor?.let { styles.add("background: $it") }
    if (this.borderRadius > 0) styles.add("border-radius: ${this.borderRadius}px")

    // Enum Eşlemeleri
    val justifyCss = when (this.justifyContent) {
        TastyJustifyContent.START -> "flex-start"
        TastyJustifyContent.CENTER -> "center"
        TastyJustifyContent.END -> "flex-end"
        TastyJustifyContent.SPACE_BETWEEN -> "space-between"
        TastyJustifyContent.SPACE_AROUND -> "space-around"
    }
    styles.add("justify-content: $justifyCss")

    val alignCss = when (this.alignItems) {
        TastyAlignItems.START -> "flex-start"
        TastyAlignItems.CENTER -> "center"
        TastyAlignItems.END -> "flex-end"
        TastyAlignItems.STRETCH -> "stretch"
    }
    styles.add("align-items: $alignCss")

    if (this.gap > 0) styles.add("gap: ${this.gap}px")

    return styles.joinToString("; ")
}