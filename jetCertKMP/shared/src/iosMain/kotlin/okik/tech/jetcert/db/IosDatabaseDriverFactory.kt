package okik.tech.jetcert.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class IosDatabaseDriverFactory: DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = NativeSqliteDriver(
        JetcertDB.Schema.synchronous(),
        "Jetcert.db"
    )
}