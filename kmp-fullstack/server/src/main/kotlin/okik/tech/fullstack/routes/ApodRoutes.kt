package okik.tech.fullstack.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import okik.tech.fullstack.models.ErrorResponse
import okik.tech.fullstack.services.ApodService
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Routes implements some patterns, DI, clean structure(try-catch), early validation, and utility
 * helpers(Exposed exec to query the db directly) gives us a routing set up that is easy to expand,
 * test and maintain
 */
fun Route.apodRoutes() {
    // injection gives us Dependency inversion making our code more testable and flexible
    val apodService by inject<ApodService>()
    val logger = LoggerFactory.getLogger("apodRoutes")


    route("/api/apod") {
        get("/today") {
            // try catch gives us clean structure
            try {
                val apod = apodService.getTodayApod()
                call.respond(apod)
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch today's APOD: ${e.message}")
            }
        }

        get("/date/{date}") {
            val date = call.parameters["date"]

            // early returns by using validations avoids deep nesting, flattens the code and makes it more readable
            if (date == null) {
                call.respondError(HttpStatusCode.BadRequest, "Missing date parameter")
                return@get
            }

            try {
                val apod = apodService.getApodByDate(LocalDate.parse(date))
                call.respond(apod)
            } catch (e: IllegalArgumentException) {
                call.respondError(HttpStatusCode.BadRequest, e.message ?: "Invalid date")
            } catch (e: Exception) {
                call.respondError(
                    HttpStatusCode.InternalServerError,
                    "Failed to fetch APOD for date $date: ${e.message}"
                )
            }
        }

        get("/random") {
            try {
                val apod = apodService.getRandomApod()
                call.respond(apod)
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch random APOD: ${e.message}")
            }
        }

        get("/history") {
            try {
                val page = call.parameters["page"]?.toUByteOrNull() ?: 1U
                val pageSize = call.parameters["pageSize"]?.toUByteOrNull() ?: 10U

                // ad start date and end date if start date is after end date and respond with 400 clear message

                if (page <= 0U || pageSize <= 0U || pageSize > 100U) {
                    call.respondError(
                        HttpStatusCode.BadRequest,
                        "Invalid pagination parameters. Page must be > 0 and pageSize must be between 1 and 100."
                    )
                    return@get
                }

                val startDateParam = call.parameters["startDate"]
                val endDateParam = call.parameters["endDate"]

                if (startDateParam == null && endDateParam == null) {
                    val history = apodService.getApodHistory(page, pageSize, null, null)
                    call.respond(history)
                    return@get
                }

                if (startDateParam == null || endDateParam == null) {
                    call.respondError(
                        HttpStatusCode.BadRequest,
                        "Invalid state. Either pass both start and end dates or pass neither"
                    )

                    return@get
                }

                val startDate = LocalDate.parse(startDateParam)
                val endDate = LocalDate.parse(endDateParam)

                if (startDate.isAfter(endDate)) {
                    call.respondError(
                        HttpStatusCode.BadRequest,
                        "Invalid state. Start date can not be before end date"
                    )

                    return@get
                }

                val histories = apodService.getApodHistory(page, pageSize, startDate, endDate)

                call.respond(histories)
            } catch (e: DateTimeParseException) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch APOD history: ${e.message}")
            } catch (e: Exception) {
                call.respondError(HttpStatusCode.InternalServerError, "Failed to fetch APOD history: ${e.message}")
            }
        }

    }

    /**
     * lets us inspect db directly, it returns a Json object with a list of tables, a success message
     * and internal connection info, it allows to validate the app is properly initialized, specially
     * in staging or test environments. It uses a raw SQL query to list table names, in this case it
     * it is more straight and makes sense to have raw SQL query here rather than using exposed's
     * Kotlin DSL language
     */
    get("/api/admin/db-status") {
        logger.info("In get - /api/admin/db-status")

        try {
            val db = Database.connect(
                url = "jdbc:sqlite:apod.db",
                driver = "org.sqlite.JDBC"
            )

            val tables = transaction(db) {
                exec("SELECT name FROM sqlite_master WHERE type='table';") { rs ->
                    generateSequence {
                        if (rs.next()) rs.getString(1) else null
                    }.toList()
                } ?: emptyList()
            }

            // Create a flat structure with only string values
            val result = mapOf(
                "status" to "success",
                "message" to "Database connected successfully",
                "dbPath" to "generated-db/apod.db",
                "tableCount" to tables.size.toString(),
                "tables" to tables.joinToString(", ")
            )

            call.respond(result)
        } catch (e: Exception) {
            call.respond(mapOf(
                "status" to "error",
                "message" to (e.message ?: "Unknown error"),
                "error" to e.toString()
            ))
        }
    }
}

suspend fun ApplicationCall.respondError(status: HttpStatusCode, message: String) {
    this.respond(status, ErrorResponse(status.value, message))
}