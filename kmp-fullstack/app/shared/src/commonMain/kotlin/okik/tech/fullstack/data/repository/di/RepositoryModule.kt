package okik.tech.fullstack.data.repository.di

import okik.tech.fullstack.data.repository.ApodRepositoryImpl
import okik.tech.fullstack.domain.ApodRepository
import org.koin.dsl.module

val repositoryModule = module {

    // Repository
    single<ApodRepository> { ApodRepositoryImpl(get()) }
}