package okik.tech.fullstack.data.network.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual val engineFactory: HttpClientEngineFactory<*> = Js