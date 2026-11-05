package org.beem.tastymap.ui.components

fun TastyIcon.getWebLabel(): String {
    return when (this) {
        TastyIcon.LOCATION -> "🎯"
        TastyIcon.ADD -> "+"
        TastyIcon.SEARCH -> "🔍"
        TastyIcon.FILTER -> "filter"
    }
}