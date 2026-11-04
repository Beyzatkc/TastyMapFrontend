package org.beem.tastymap.core.util

fun Int.formatLikeCount(): String = this.toLong().formatLikeCount()

fun Long.formatLikeCount(): String {
    if(this < 1000) return this.toString()

    return when{
        this >= 1_000_000 -> {
            val integerPart = this / 1_000_000
            val decimalPart = (this % 1_000_000) / 100_000

            if(decimalPart > 0L){
                "$integerPart.${decimalPart}m"
            }else{
                 "${integerPart}m"
            }
        }else -> {
            val integerPart = this / 1_000
            val decimalPart = (this % 1_000) / 100

            if (decimalPart > 0L) {
                "$integerPart.${decimalPart}k"
            } else {
                "${integerPart}k"
            }
        }
    }
}