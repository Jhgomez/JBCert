package okik.tech.jetcert.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import okik.tech.jetcert.db.JetcertDB
import org.w3c.dom.MODULE
import org.w3c.dom.Worker
import org.w3c.dom.WorkerOptions
import org.w3c.dom.WorkerType

import kotlin.js.js



suspend fun initKoinWeb() {
    val url = js(""""@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url""")

    val worker = Worker(url)

    val sqlDriver = WebWorkerDriver(worker)


    JetcertDB.Schema.create(sqlDriver).await()

    val platformModule: Module = module {
        single<SqlDriver>(definition = { sqlDriver })
    }

    initKoin {
        printLogger()
        modules(platformModule)
    }
}