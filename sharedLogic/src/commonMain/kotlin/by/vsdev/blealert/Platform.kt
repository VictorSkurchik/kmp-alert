package by.vsdev.blealert

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform