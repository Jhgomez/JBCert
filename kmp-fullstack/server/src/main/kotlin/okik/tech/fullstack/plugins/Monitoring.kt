package okik.tech.fullstack.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

/**
 * used in call logging, this adds structured request logging helpful in debugging and observability,
 * it logs times like "io.ktor.server.Application - 200 OK: GET - /api/apod/today in 88ms" this request
 * logs can tell us where delays are happening. You could add correlation IDs to each request which will
 * allow to trace a request across services and even down to specific DB queries. The apod service
 * logs cache hit rates, it logs whether an register was pulled from NASA's servers or local cache,
 * which tells us if our caching strategy is working. Simple logging can help start performance optimizations
 * this means full observability is not, just simple logging done consistently can be helpful to identify
 * trends, isolate routes and understand cost of different operations
 */
fun Application.configureMonitoring() {
    install (CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/api/") // /api/ matches our endpoints so we can monitor incomming request
        }
    }
}
