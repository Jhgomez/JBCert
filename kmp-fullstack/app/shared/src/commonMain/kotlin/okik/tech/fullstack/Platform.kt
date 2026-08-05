package okik.tech.fullstack

interface Platform {
    fun getBaseUrl(): String
}

expect fun getPlatform(): Platform