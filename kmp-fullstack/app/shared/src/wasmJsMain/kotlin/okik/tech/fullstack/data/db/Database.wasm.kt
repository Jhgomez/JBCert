package okik.tech.fullstack.data.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import okik.tech.fullstack.db.FullstackDb
import org.w3c.dom.Worker

@OptIn(ExperimentalWasmJsInterop::class)
val worker: Worker = js(
    """new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))"""
)

class IosDbDriverProducerImpl(): DbDriverFactory {
    override suspend fun asynchronousGet(): SqlDriver {
        val driver = WebWorkerDriver(worker)

        FullstackDb.Schema.awaitCreate(driver)

        return  driver
    }
}