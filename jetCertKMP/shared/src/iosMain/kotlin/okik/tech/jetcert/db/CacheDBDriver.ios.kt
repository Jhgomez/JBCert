package okik.tech.jetcert.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(schema = JetcertDB.Schema.synchronous(), name = "JetCert.db")
}