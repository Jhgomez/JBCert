package okik.tech.daggerexample.data.repository

import okik.tech.daggerexample.data.service.FakeServiceA
import okik.tech.daggerexample.domain.FakeRepoA

class FakeRepoAImpl(private val service: FakeServiceA): FakeRepoA {
    override fun execute(): String = "Fake Repo A Consuming: ${service.execute()}"
}