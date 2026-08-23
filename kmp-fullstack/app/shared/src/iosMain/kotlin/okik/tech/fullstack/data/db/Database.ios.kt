package okik.tech.fullstack.data.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import okik.tech.fullstack.db.FullstackDb


class IosDbDriverProducerImpl(): DbDriverFactory {
    override fun synchronousGet(): SqlDriver =
        NativeSqliteDriver(
            schema = FullstackDb.Schema.synchronous(),
            name = "fullstackDb"
        )
}