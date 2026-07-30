package okik.tech.jetcert.di

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import okik.tech.jetcert.db.JetcertDB
import org.koin.core.module.Module
import org.koin.dsl.module
import org.w3c.dom.Worker

@OptIn(ExperimentalWasmJsInterop::class)
val worker: Worker = js(
    """new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))"""
)
actual suspend fun koinInit() {
    val sqlDriver = WebWorkerDriver(worker)

    JetcertDB.Schema.awaitCreate(sqlDriver)

    val platformModule: Module = module {
        single<SqlDriver>(definition = { sqlDriver })
    }

    initKoin {
        printLogger()
        modules(platformModule)
    }
}