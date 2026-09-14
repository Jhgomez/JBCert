package okik.tech.daggerexample.data.repository

import okik.tech.daggerexample.data.service.FakeServiceA
import okik.tech.daggerexample.domain.FakeRepoA
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepoAImpl @Inject constructor(private val service: FakeServiceA): FakeRepoA {
    override fun execute(): String = "Fake Repo A Consuming: ${service.execute()}"
}