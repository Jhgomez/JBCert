package okik.tech.fullstack.data.db

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.db.FullstackDb

interface DbDriverFactory {
    // This is used in all targets but web
    fun synchronousGet(): SqlDriver? = null

    // This is used in Web targets
    suspend fun asynchronousGet(): SqlDriver? = null
}

fun createDatabase(sqlDriver: SqlDriver) : FullstackDb {
    return FullstackDb(sqlDriver)
}