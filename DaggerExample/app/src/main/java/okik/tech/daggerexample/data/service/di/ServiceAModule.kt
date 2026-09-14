package okik.tech.daggerexample.data.service.di

import dagger.Binds
import okik.tech.daggerexample.data.service.FakeServiceA
import okik.tech.daggerexample.data.service.FakeServiceAImpl
import javax.inject.Singleton

@Singleton
interface ServiceAModule {

    @Singleton
    @Binds
    fun bindsFakeService(serviceImpl: FakeServiceAImpl): FakeServiceA
}