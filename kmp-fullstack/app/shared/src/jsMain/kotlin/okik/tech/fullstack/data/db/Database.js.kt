package okik.tech.fullstack.data.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import okik.tech.fullstack.db.FullstackDb
import org.w3c.dom.Worker

class JsDbDriverProducerImpl(): DbDriverFactory {
    override suspend fun asynchronousGet(): SqlDriver {
        val sqlDriver = WebWorkerDriver(
            Worker(
                js(
                    """new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""
                )
            )
        )

        FullstackDb.Schema.awaitCreate(sqlDriver)

        return sqlDriver
    }
}