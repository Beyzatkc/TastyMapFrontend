package org.beem.tastymap.ui.tastyview


import androidx.compose.ui.graphics.Color

fun String.toComposeColor(): Color {
    val hex = this.replace("#", "")

    return when (hex.length) {
        6 -> {
            Color("FF$hex".toLong(16))
        }
        8 -> {
            Color(hex.toLong(16))
        }
        else -> Color.White
    }
}