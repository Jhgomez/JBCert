package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import okik.tech.fullstack.db.FullstackDb
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.Properties

actual val driverModule: Module = module {
    single<SqlDriver> {
        JdbcSqliteDriver(
            url = "jdbc:sqlite:/path/to/myDatabase.db ", //JdbcSqliteDriver.IN_MEMORY,
            properties = Properties(),
            schema = FullstackDb.Schema.synchronous()
        )
    }
}