package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.data.db.JsDbDriverProducerImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual suspend fun getDriverModule(): Module {
    val driver = JsDbDriverProducerImpl().asynchronousGet()

    return module {
        single<SqlDriver> { driver }
    }
}