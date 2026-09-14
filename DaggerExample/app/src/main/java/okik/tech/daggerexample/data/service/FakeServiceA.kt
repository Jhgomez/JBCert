package okik.tech.daggerexample.data.service

import okik.tech.daggerexample.client.FakeClient
import javax.inject.Inject
import javax.inject.Singleton

interface FakeServiceA {
    fun execute(): String
}

@Singleton
class FakeServiceAImpl @Inject constructor(private val client: FakeClient): FakeServiceA {
    override fun execute(): String = "Fake Service A Consuming - ${client.execute()}"
}