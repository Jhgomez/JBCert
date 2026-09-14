package okik.tech.daggerexample.data.service.di

import dagger.Binds
import dagger.Component
import dagger.Module
import okik.tech.daggerexample.data.service.FakeServiceA
import okik.tech.daggerexample.data.service.FakeServiceAImpl
import javax.inject.Singleton

@Module
interface ServiceAModule {

    @Singleton
    @Binds
    fun bindsFakeService(serviceImpl: FakeServiceAImpl): FakeServiceA
}