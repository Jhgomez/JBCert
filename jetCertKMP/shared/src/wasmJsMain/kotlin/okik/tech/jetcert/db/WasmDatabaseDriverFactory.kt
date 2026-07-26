package okik.tech.jetcert.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

@OptIn(ExperimentalWasmJsInterop::class)
fun jsWorker(): Worker = js(
    """new URL("cashapp/sqldelight-sqljs-worker/sqljs.worker.js, import.meta.url")"""
)

class WasmDatabaseDriverFactory: DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = WebWorkerDriver(jsWorker())
}