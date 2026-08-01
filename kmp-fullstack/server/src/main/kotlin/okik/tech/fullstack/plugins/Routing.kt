package okik.tech.fullstack.plugins

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            val javaVersion = System.getProperty("java.version")
            call.respondText("Ktor: Hello, Java $javaVersion!")
        }
        get("/health") {
            call.respondText("Server is running")
        }
    }
}