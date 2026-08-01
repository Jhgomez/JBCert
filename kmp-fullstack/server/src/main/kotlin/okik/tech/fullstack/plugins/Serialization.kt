package okik.tech.fullstack.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configureSerialization(json: (Any?) -> Unit) {
    // content negotiation allows us to do serialization, the json block is the format we are
    // using in our serialized data, it could be xml or protobuf alternatively if needed
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}