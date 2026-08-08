package okik.tech.fullstack

import kotlin.jvm.JvmInline

@JvmInline
value class Platform(private val baseUrl: String) {
    fun getHostName(): String = baseUrl.substring(0, baseUrl.indexOf(":"))

    fun getPort(): UShort = baseUrl.substring(baseUrl.indexOf(":") + 1).toUShort()
}

expect fun getPlatform(): Platform