package okik.tech.fullstack.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

fun HttpClientConfig<CIOEngineConfig>.configureHttpResponseValidator() {
    expectSuccess = true

    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, request ->
            when (exception) {
                is ClientRequestException -> {
                    val exceptionResponse = exception.response
                    val exceptionResponseText = exceptionResponse.bodyAsText()

                    when (exceptionResponse.status) {
                        HttpStatusCode.NotFound -> {
                            throw NotFound(exceptionResponseText)
                        }

                        HttpStatusCode.Unauthorized -> {
                            throw Unauthorized(exceptionResponseText)
                        }
                    }
                }
            }
        }
    }
}

data class NotFound(val reason: String): Exception(message = reason)
data class Unauthorized(val reason: String): Exception(message = reason)
