package okik.tech.fullstack.data.db.di

import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.data.db.ApodDao
import okik.tech.fullstack.data.db.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val driverModule: Module
value class DaoDispatcher(val dispatcher: CoroutineDispatcher)

val databaseModule = module {
    includes(driverModule)

    single { createDatabase(sqlDriver = get()) }

    single {
        val daoDispatcher: DaoDispatcher = get()
        ApodDao(database = get(), dispatcher = daoDispatcher.dispatcher)
    }
}