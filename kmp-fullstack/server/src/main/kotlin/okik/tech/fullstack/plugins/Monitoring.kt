package okik.tech.fullstack.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

// used in call logging, this adds structured request logging helpful in debugging and observability
fun Application.configureMonitoring() {
    install (CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/api/") // /api/ matches our endpoints so we can monitor incomming request
        }
    }
}
