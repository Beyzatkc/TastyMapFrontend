package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScoreType(val displayName: String) {
    OVERALL("Genel Değerlendirme"),
    TASTE("Lezzet"),
    WAITING_TIME("Hız / Bekleme Süresi"),
    SERVICE("Servis Kalitesi"),
    HOSPITALITY("Misafirperverlik"),
    PRICE_PERFORMANCE("Fiyat / Performans"),
    CLEANLINESS("Temizlik & Hijyen"),
    LOCATION("Konum & Ulaşım"),
    INTERIOR_DESIGN("Mekan & Atmosfer")
}