package okik.tech.fullstack.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    install(CORS) {
        // Allow specific origins (replace with your frontend URL in production)
        allowHost("localhost:8080") // Example frontend origin
        // the IP assign to you by your DHCP server(AKA, your ISP router) or change it to "localhost"
        // so other devices connected to my router can fetch also
        allowHost("192.168.1.15:8080")
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