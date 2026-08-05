package okik.tech.fullstack

import kotlin.jvm.JvmInline

@JvmInline
value class WebPlatform(private val baseUrl: String): Platform {
    override fun getBaseUrl(): String = baseUrl
}

actual fun getPlatform(): Platform = WebPlatform(RemoteServerConfig.WEB_URL)