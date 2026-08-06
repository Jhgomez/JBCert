package okik.tech.fullstack.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okik.tech.fullstack.getPlatform

object NetworkConfig {
    val DEFAULT_BASE_URL = getPlatform().getBaseUrl()

    fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                    coerceInputValues = true
                })
            }

            install(Logging) {
                logger = Logger.SIMPLE // logs straight to the web console
                level = LogLevel.INFO
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10000  // 10 seconds
                connectTimeoutMillis = 5000   // 5 seconds
            }
        }
    }
}