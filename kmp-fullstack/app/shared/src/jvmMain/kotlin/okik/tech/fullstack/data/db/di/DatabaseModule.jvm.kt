package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import okik.tech.fullstack.db.FullstackDb
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

actual val driverModule: Module = module {
    single { IoDispatcher(Dispatchers.IO) }

    single<SqlDriver> {
        // sqldelight can create a missing file but not missing folder so be careful here and create
        // if doesn't exists
        val dbFilePath = Path.of("../../path/to/myDatabase.db")
        if (!Files.exists(dbFilePath)) {
            Files.createDirectories(dbFilePath.parent)
            Files.createFile(dbFilePath)
        }

        JdbcSqliteDriver(
            url = "jdbc:sqlite:$dbFilePath", //JdbcSqliteDriver.IN_MEMORY,
            properties = Properties(),
            schema = FullstackDb.Schema.synchronous()
        )
    }
}