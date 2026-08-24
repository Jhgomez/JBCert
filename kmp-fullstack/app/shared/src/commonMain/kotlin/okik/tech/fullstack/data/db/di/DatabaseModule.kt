package okik.tech.fullstack.data.db.di

import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.data.db.DbTransaction
import okik.tech.fullstack.data.db.DbTransactionImpl
import okik.tech.fullstack.data.db.dao.ApodDao
import okik.tech.fullstack.data.db.createDatabase
import okik.tech.fullstack.data.db.dao.ApodDaoImpl
import okik.tech.fullstack.data.db.dao.PagingInfoDao
import okik.tech.fullstack.data.db.dao.PagingInfoDaoImpl
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.jvm.JvmInline

expect val driverModule: Module
@JvmInline
value class IoDispatcher(val dispatcher: CoroutineDispatcher)

val databaseModule = module {
    includes(driverModule)

    single { createDatabase(sqlDriver = get()) }

    single<DbTransaction> { DbTransactionImpl(database = get()) }

    single<ApodDao> {
        val daoDispatcher: IoDispatcher = get()
        ApodDaoImpl(
            database = get(),
            dispatcher = daoDispatcher.dispatcher
        )
    }

    single<PagingInfoDao> {
        PagingInfoDaoImpl(database = get())
    }
}