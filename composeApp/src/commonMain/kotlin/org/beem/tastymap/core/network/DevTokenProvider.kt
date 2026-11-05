package org.beem.tastymap.core.network

object DevTokenProvider {
    // Bruno'dan kopyaladığın Bearer token'ı buraya yapıştır
    var token: String = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZSI6IlVTRVIiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNzg3MzI2NTI3LCJleHAiOjE3OTYzMjY1Mjd9.wYRh9cGMsS-sXxY9vWz14lxLWMLE1RGwwtEEwpBCH6U"

    val authHeader: String?
        get() = token.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
}