package okik.tech.jetcert.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import eu.anifantakis.lib.ksafe.KSafe
import okik.tech.jetcert.db.JetcertDB
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun init() {
    val driver = NativeSqliteDriver(
        JetcertDB.Schema.synchronous(),
        "Jetcert.db"
    )

    val iosModule: Module = module {
        // Classic DSL with lambda for custom construction
//        single { driver } bind SqlDriver::class

        single<SqlDriver>(definition = { driver }) // Compiler DSL

        single(named("prefs")) {
            KSafe(fileName = "prefs")
        }

        single(named("vault")) {
            KSafe(fileName = "vault")
        }
    }

    initKoin {
        modules(iosModule)
    }
}