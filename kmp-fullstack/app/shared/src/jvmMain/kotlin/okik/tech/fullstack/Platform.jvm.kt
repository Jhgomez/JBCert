package okik.tech.fullstack

@JvmInline
value class DesktopPlatform(private val baseUrl: String): Platform {
    override fun getBaseUrl(): String = baseUrl
}

actual fun getPlatform(): Platform = DesktopPlatform(RemoteServerConfig.DESKTOP_URL)