package okik.tech.jetcert.di

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import okik.tech.jetcert.db.JetcertDB
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module


fun SetUpKoin(config: KoinAppDeclaration? = null) {
    fun getDriver(context: Context): SqlDriver = AndroidSqliteDriver(
        JetcertDB.Schema.synchronous(),
        context,
        "Jetcert.db"
    )

    val androidKoinModule = module {
        // single<SqlDriver>(definition = { getDriver(get()) })  compiler DSL
        // single { getDriver(get()) } bind SqlDriver::get       Classic DSL

        single { getDriver(get()) } // Classic DSL
    }

    initKoin {
        includes(config)
        modules(androidKoinModule)
    }
}