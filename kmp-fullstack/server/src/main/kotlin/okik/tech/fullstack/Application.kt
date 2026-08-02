package okik.tech.fullstack

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import okik.tech.fullstack.config.DatabaseConfig.databaseModule
import okik.tech.fullstack.config.appConfigModule
import okik.tech.fullstack.config.initializeDatabase
import okik.tech.fullstack.plugins.configureMonitoring
import okik.tech.fullstack.plugins.configureRouting
import okik.tech.fullstack.plugins.configureSerialization
import okik.tech.fullstack.plugins.configureStatusPages
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(
        Netty, // is an engine
        // this allows to override the PORT variable viea environment
        // vars which will make easier to deploy to different environments latter
        port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    )
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        //        fileProperties("/application.conf")
        modules(appConfigModule, databaseModule,
//            appModule
        )
    }
//
    initializeDatabase()
    configureSerialization()
    configureStatusPages()
    configureMonitoring()
    configureRouting()
//    configureBacgroundJobs()
}