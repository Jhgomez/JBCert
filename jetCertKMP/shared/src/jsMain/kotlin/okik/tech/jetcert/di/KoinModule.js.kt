package okik.tech.jetcert.di

import okik.tech.jetcert.db.DatabaseDriverFactory
import okik.tech.jetcert.db.JsDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
    // Classic DSL with lambda for custom construction
//    single { JsDatabaseDriverFactory() } bind DatabaseDriverFactory::class
    single<JsDatabaseDriverFactory>() bind DatabaseDriverFactory::class
}