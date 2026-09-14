package okik.tech.daggerexample.client.di

import dagger.Module
import dagger.Provides
import okik.tech.daggerexample.client.FakeClient
import javax.inject.Singleton

@Module
object ClientModule {

    @Singleton
    @Provides
    fun providesClient() = FakeClient()
}