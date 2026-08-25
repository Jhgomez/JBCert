package okik.tech.fullstack.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    install(CORS) {
        // Allow specific origins (replace with your frontend URL in production)
        allowHost("localhost:3000") // Example frontend origin
        // Or use anyHost() for loose development access:
        // anyHost()

        // Allow standard HTTP methods
        HttpMethod.DefaultMethods.forEach { allowMethod(it) }

        // Allow common headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)

        // Allow credentials (cookies, authorization headers) if needed
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
}