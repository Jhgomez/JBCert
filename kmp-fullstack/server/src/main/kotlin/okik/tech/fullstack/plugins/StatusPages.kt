package okik.tech.fullstack.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import okik.tech.fullstack.models.ErrorResponse

/**
 * lets you catch exceptions and translate into well-formed http responses, instead of returning a
 * stack trace or an internal server error you can catch SPECIFIC exceptions like a not found exception
 * and respond with a meaningful message and status code
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(
                    HttpStatusCode.BadRequest.value,
                    cause.message ?: "Invalid request"
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    HttpStatusCode.InternalServerError.value,
                    "An internal error occurred. Please try again later."
                )
            )
        }

        // if your service responds with a NotFound status code(404), that response is intercepted here
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    HttpStatusCode.NotFound.value,
                    "The requested resource was not found"
                )
            )
        }
    }
}

