package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.data.db.JvmDbDriverProducerImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single<SqlDriver> { JvmDbDriverProducerImpl().synchronousGet() }
}