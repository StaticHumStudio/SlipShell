package com.statichumstudio.slipshell

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
