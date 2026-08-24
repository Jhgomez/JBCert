package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import okik.tech.fullstack.db.FullstackDb
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single<DaoDispatcher> { DaoDispatcher(Dispatchers.IO) }

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = FullstackDb.Schema.synchronous(),
            context = androidContext(),
            name = "fullstackDb"
        )
    }


}
