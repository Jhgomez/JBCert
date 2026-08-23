package okik.tech.fullstack.data.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import okik.tech.fullstack.db.FullstackDb
import java.util.Properties


class JvmDbDriverProducerImpl(): DbDriverFactory {
    override fun synchronousGet(): SqlDriver =
        JdbcSqliteDriver(
            url = "jdbc:sqlite:/path/to/myDatabase.db ", //JdbcSqliteDriver.IN_MEMORY,
            properties = Properties(),
            schema = FullstackDb.Schema.synchronous()
        )
}