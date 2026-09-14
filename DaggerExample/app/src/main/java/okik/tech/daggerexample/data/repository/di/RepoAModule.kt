package okik.tech.daggerexample.data.repository.di

import dagger.Binds
import okik.tech.daggerexample.data.repository.FakeRepoAImpl
import okik.tech.daggerexample.data.service.FakeServiceA
import okik.tech.daggerexample.data.service.FakeServiceAImpl
import okik.tech.daggerexample.domain.FakeRepoA
import javax.inject.Singleton

@Singleton
interface RepoAModule {

    @Singleton
    @Binds
    fun bindsFakeRepo(repo: FakeRepoAImpl): FakeRepoA
}