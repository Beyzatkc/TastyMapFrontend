package org.beem.tastymap.ui.tastyview

fun TastyModifier.toCssStyle(): String {
    val styles = mutableListOf("display: ${this.display}", "flex-direction: ${this.flexDirection}")
    if (this.fillMaxWidth) styles.add("width: 100%")
    if (this.width != null) styles.add("width: ${this.width}")
    if (this.height != null) styles.add("height: ${this.height}")
    if (this.padding > 0) styles.add("padding: ${this.padding}px")
    if (this.paddingBottom > 0) styles.add("padding-bottom: ${this.paddingBottom}px")
    if (this.marginTop > 0) styles.add("margin-top: ${this.marginTop}px")
    if (this.marginBottom > 0) styles.add("margin-bottom: ${this.marginBottom}px")
    this.backgroundColor?.let { styles.add("background: $it") }
    if (this.borderRadius > 0) styles.add("border-radius: ${this.borderRadius}px")
    this.justifyContent?.let { styles.add("justify-content: $it") }
    this.alignItems?.let { styles.add("align-items: $it") }
    if (this.gap > 0) styles.add("gap: ${this.gap}px")
    if (this.cursor != null) styles.add("cursor: ${this.cursor}")
    return styles.joinToString("; ")
}