package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okik.tech.fullstack.db.FullstackDb
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single { IoDispatcher(Dispatchers.IO) }

    single<SqlDriver> {
        NativeSqliteDriver(
            schema = FullstackDb.Schema.synchronous(),
            name = "fullstackDb"
        )
    }
}