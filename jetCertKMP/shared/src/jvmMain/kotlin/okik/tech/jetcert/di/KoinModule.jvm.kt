package okik.tech.jetcert.di

import okik.tech.jetcert.db.DatabaseDriverFactory
import okik.tech.jetcert.db.JvmDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
    // Classic DSL with lambda for custom construction
//    single { JvmDatabaseDriverFactory() } bind DatabaseDriverFactory::class
    single<JvmDatabaseDriverFactory>() bind DatabaseDriverFactory::class
 }