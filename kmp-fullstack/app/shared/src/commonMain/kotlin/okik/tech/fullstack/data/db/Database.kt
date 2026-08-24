package okik.tech.fullstack.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.logs.LogSqliteDriver
import okik.tech.fullstack.Logger
import okik.tech.fullstack.db.FullstackDb

fun createDatabase(sqlDriver: SqlDriver) : FullstackDb {

    val driverLoggerWrapper = LogSqliteDriver(
        sqlDriver = sqlDriver,
        logger = { message -> Logger.logInfo("SqlDelightLogging", message) }
    )

    return FullstackDb(driverLoggerWrapper)
}