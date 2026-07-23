package okik.tech.jetcert

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform