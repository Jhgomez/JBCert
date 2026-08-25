package okik.tech.fullstack.data.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okik.tech.fullstack.getPlatform

expect val engineFactory: HttpClientEngineFactory<*>

object NetworkConfig {
    val platform = getPlatform()

    // Ktor doesn't handle non Http responses with non 2xx http status codes, in kotlin Retrofit
    // throws an exception automatically when code is not a 2xx, Ktor client offers us a similar
    // functionality but also a very nice way to customize them, both are documented here:
    // https://ktor.io/docs/client-response-validation.html
    fun createHttpClient(): HttpClient {
        return HttpClient(engineFactory) {
            defaultRequest {
                url.protocol = URLProtocol.HTTP

                // Set the domain name/host
                url.host = platform.getHostName()

                url.port = platform.getPort().toInt()
            }

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

            configureHttpResponseValidator()
        }
    }
}