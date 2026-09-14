package okik.tech.daggerexample.data.service

import okik.tech.daggerexample.client.FakeClient

interface FakeServiceA {
    fun execute(): String
}

class FakeServiceAImpl(private val client: FakeClient): FakeServiceA {
    override fun execute(): String = "Fake Service A Consuming - ${client.execute()}"
}