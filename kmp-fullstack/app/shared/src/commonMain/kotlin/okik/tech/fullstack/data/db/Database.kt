package okik.tech.fullstack.data.db

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.db.FullstackDb

fun createDatabase(sqlDriver: SqlDriver) : FullstackDb {
    return FullstackDb(sqlDriver)
}