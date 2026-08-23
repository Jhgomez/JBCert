package okik.tech.fullstack.data.db.di

import okik.tech.fullstack.data.db.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val driverModule: Module

val databaseModule = module {
    includes(driverModule)

    single { createDatabase(driverFactory = get()) }
}