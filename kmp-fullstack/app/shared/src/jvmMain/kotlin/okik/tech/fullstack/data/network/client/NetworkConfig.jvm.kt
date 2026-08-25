package okik.tech.fullstack.data.network.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual val engineFactory: HttpClientEngineFactory<*> = OkHttp