package okik.tech.fullstack.data.db.di

import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.data.db.dao.ApodDao
import okik.tech.fullstack.data.db.createDatabase
import okik.tech.fullstack.data.db.dao.ApodDaoImpl
import okik.tech.fullstack.data.db.dao.PagingInfoDao
import okik.tech.fullstack.data.db.dao.PagingInfoDaoImpl
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.PageInfoEntity
import org.koin.core.module.Module
import org.koin.dsl.module

expect val driverModule: Module
value class DaoDispatcher(val dispatcher: CoroutineDispatcher)

val databaseModule = module {
    includes(driverModule)

    single { createDatabase(sqlDriver = get()) }

    single<ApodDao> {
        val daoDispatcher: DaoDispatcher = get()
        ApodDaoImpl(database = get(), dispatcher = daoDispatcher.dispatcher)
    }

    single<PagingInfoDao> {
        PagingInfoDaoImpl(database = get())
    }
}