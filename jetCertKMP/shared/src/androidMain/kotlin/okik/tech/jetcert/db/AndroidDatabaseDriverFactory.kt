package okik.tech.jetcert.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = AndroidSqliteDriver(
        JetcertDB.Schema.synchronous(),
        context,
        "Jetcert.db"
    )
}