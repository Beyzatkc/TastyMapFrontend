package org.beem.tastymap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform