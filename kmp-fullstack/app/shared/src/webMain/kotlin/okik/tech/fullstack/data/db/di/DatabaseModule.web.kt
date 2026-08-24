package okik.tech.fullstack.data.db.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

// empty module since web targets can not create a sql driver synchronously, it has to be
// asynchronously and we should not force other targets to create it synchronously
actual val driverModule: Module = module {
    single { DaoDispatcher(Dispatchers.Default) }
}

expect suspend fun getDriverModule(): Module