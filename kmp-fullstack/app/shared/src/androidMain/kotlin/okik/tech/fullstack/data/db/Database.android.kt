package okik.tech.fullstack.data.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import okik.tech.fullstack.db.FullstackDb


class AndroidDbDriverProducerImpl(private val context: Context): DbDriverFactory {
    override fun synchronousGet(): SqlDriver =
        AndroidSqliteDriver(
            schema = FullstackDb.Schema.synchronous(),
            context = context,
            name = "fullstackDb"
        )
}