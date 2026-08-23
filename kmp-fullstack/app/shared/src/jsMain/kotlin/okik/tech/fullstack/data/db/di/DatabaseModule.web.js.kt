package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import okik.tech.fullstack.db.FullstackDb
import org.koin.core.module.Module
import org.koin.dsl.module
import org.w3c.dom.Worker

actual suspend fun getDriverModule(): Module {
    val sqlDriver = WebWorkerDriver(
        Worker(
            js(
                """new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""
            )
        )
    )

    FullstackDb.Schema.awaitCreate(sqlDriver)

    return module {
        single<SqlDriver> { sqlDriver }
    }
}