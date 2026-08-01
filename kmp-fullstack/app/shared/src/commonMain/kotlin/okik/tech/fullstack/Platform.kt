package okik.tech.fullstack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform