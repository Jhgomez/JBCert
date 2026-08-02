package okik.tech.fullstack.services


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okik.tech.fullstack.models.ApodResponse
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

private const val HOURLY_LIMIT = 30
private const val DAILY_LIMIT = 50
private const val MIN_REQUEST_INTERVAL = 1000L

private fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
        })
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}

class NasaApiClient(
    private val apiKey: String,
    private val httpClient: HttpClient = createHttpClient()
) {
    private val logger = LoggerFactory.getLogger(NasaApiClient::class.java)
    private val baseUrl = "https://api.nasa.gov/planetary/apod"

    private val requestMutex = Mutex()
    private val hourlyRequestCounter = AtomicInteger(0)
    private val dailyRequestCounter = AtomicInteger(0)
    private var hourlyWindowStartTime = System.currentTimeMillis()
    private var dailyWindowStartTime = System.currentTimeMillis()

    // Last request time
    private var lastRequestTime = 0L

    suspend fun getTodayApod(): ApodResponse {
        logger.info("Fetching today's APOD from NASA API")
        return fetchSingleApod()
    }

    suspend fun getApodByDate(date: String): ApodResponse {
        logger.info("Fetching APOD for date $date from NASA API")
        return fetchSingleApod(date = date)
    }

    suspend fun getRandomApod(): ApodResponse {
        logger.info("Fetching random APOD from NASA API")
        return fetchRandomApod(1).first()
    }

    suspend fun getRandomApods(count: UByte): List<ApodResponse> {
        if (count !in 1U..100U) {
            throw IllegalArgumentException("Count must be between 1 and 100")
        }

        logger.info("Fetching $count random APODs from NASA API")
        return fetchRandomApod(count.toInt())
    }

    private suspend fun fetchSingleApod(date: String? = null): ApodResponse {
        return requestMutex.withLock {
            enforceRateLimits()

            val response = httpClient.get(baseUrl) {
                url {
                    parameters.append("api_key", apiKey)
                    date?.let { parameters.append("date", it) }
                }
            }

            handleResponse(response)
        }
    }

    private suspend fun fetchRandomApod(count: Int): List<ApodResponse> {
        return requestMutex.withLock {
            enforceRateLimits()

            val response = httpClient.get(baseUrl) {
                url {
                    parameters.append("api_key", apiKey)
                    parameters.append("count", count.toString())
                }
            }

            handleResponse(response)
        }
    }

    private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        // Update rate limiting counters
        updateRateLimitCounters()

        return when {
            response.status.isSuccess() -> response.body()
            response.status == HttpStatusCode.TooManyRequests -> {
                val retryAfter = response.headers["Retry-After"]?.toLongOrNull() ?: 60L
                logger.warn("Rate limit exceeded. Retrying after $retryAfter seconds.")
                delay(retryAfter * 1000)
                throw NasaApiException("Rate limit exceeded. Please try again later.")
            }
            else -> {
                logger.error("NASA API returned error: ${response.status}")
                throw NasaApiException("NASA API returned error: ${response.status}")
            }
        }
    }

    private fun updateRateLimitCounters() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - hourlyWindowStartTime > 3600000) { // 1 hour in milliseconds
            hourlyWindowStartTime = currentTime
            hourlyRequestCounter.set(0)
        }

        if (currentTime - dailyWindowStartTime > 86400000) { // 24 hours in milliseconds
            dailyWindowStartTime = currentTime
            dailyRequestCounter.set(0)
        }

        hourlyRequestCounter.incrementAndGet()
        dailyRequestCounter.incrementAndGet()

        lastRequestTime = currentTime
    }

    private suspend fun enforceRateLimits() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - hourlyWindowStartTime > 3600000) {
            hourlyWindowStartTime = currentTime
            hourlyRequestCounter.set(0)
        }

        if (currentTime - dailyWindowStartTime > 86400000) {
            dailyWindowStartTime = currentTime
            dailyRequestCounter.set(0)
        }

        val timeSinceLastRequest = currentTime - lastRequestTime
        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL) {
            val waitTime = MIN_REQUEST_INTERVAL - timeSinceLastRequest
            logger.debug("Rate limiting: Waiting $waitTime ms between requests")
            delay(waitTime)
        }

        if (hourlyRequestCounter.get() >= HOURLY_LIMIT * 0.9) { // 90% of limit
            val timeUntilReset = 3600000 - (currentTime - hourlyWindowStartTime)
            val waitTime = timeUntilReset / (HOURLY_LIMIT - hourlyRequestCounter.get() + 1)
            logger.warn("Approaching hourly rate limit (${hourlyRequestCounter.get()}/$HOURLY_LIMIT). Slowing down.")
            delay(waitTime)
        }

        if (dailyRequestCounter.get() >= DAILY_LIMIT * 0.9) { // 90% of limit
            val timeUntilReset = 86400000 - (currentTime - dailyWindowStartTime)
            val waitTime = timeUntilReset / (DAILY_LIMIT - dailyRequestCounter.get() + 1)
            logger.warn("Approaching daily rate limit (${dailyRequestCounter.get()}/$DAILY_LIMIT). Slowing down.")
            delay(waitTime)
        }

        if (hourlyRequestCounter.get() >= HOURLY_LIMIT) {
            val timeUntilReset = 3600000 - (currentTime - hourlyWindowStartTime)
            logger.error("Hourly rate limit reached. Waiting for reset in ${timeUntilReset/1000} seconds.")
            delay(timeUntilReset + 1000) // Wait until reset plus a small buffer
            hourlyWindowStartTime = System.currentTimeMillis()
            hourlyRequestCounter.set(0)
        }

        if (dailyRequestCounter.get() >= DAILY_LIMIT) {
            val timeUntilReset = 86400000 - (currentTime - dailyWindowStartTime)
            logger.error("Daily rate limit reached. Waiting for reset in ${timeUntilReset/1000} seconds.")
            delay(timeUntilReset + 1000) // Wait until reset plus a small buffer
            dailyWindowStartTime = System.currentTimeMillis()
            dailyRequestCounter.set(0)
        }
    }

    fun close() {
        httpClient.close()
    }
}

class NasaApiException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)