package org.beem.tastymap.ui.tastyview.to

import org.beem.tastymap.ui.tastyview.TastyModifier

fun TastyModifier.toHtmlAttributes(): String {
    return if (!this.internalId.isNullOrBlank()) {
        "id=\"${this.internalId}\""
    } else {
        ""
    }
}