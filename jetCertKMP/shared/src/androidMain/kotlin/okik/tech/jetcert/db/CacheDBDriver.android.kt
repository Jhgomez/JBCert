package okik.tech.jetcert.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import okik.tech.jetcert.appContext

actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
    JetcertDB.Schema.synchronous(),
    appContext!!,
                "Jetcert.db"
)