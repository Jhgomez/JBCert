package okik.tech.fullstack.plugins

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import okik.tech.fullstack.routes.apodRoutes

fun Application.configureRouting() {
    routing {
        apodRoutes()
    }
}