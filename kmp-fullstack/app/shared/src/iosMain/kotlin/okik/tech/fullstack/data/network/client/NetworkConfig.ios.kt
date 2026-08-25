package okik.tech.fullstack.data.network.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val engineFactory: HttpClientEngineFactory<*> = Darwin