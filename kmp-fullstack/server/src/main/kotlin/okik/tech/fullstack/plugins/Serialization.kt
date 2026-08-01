package okik.tech.fullstack.plugins

import io.ktor.server.routing.RoutingRoot.Plugin.install
import javafx.application.Application

fun Application.configureSerialization() {
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