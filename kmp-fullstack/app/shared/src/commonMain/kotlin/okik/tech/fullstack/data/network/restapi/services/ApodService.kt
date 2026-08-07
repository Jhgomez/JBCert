package okik.tech.fullstack.network.restapi.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import okik.tech.fullstack.network.restapi.ApiResult
import okik.tech.fullstack.network.restapi.util.safeRequest

class ApodApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {

    suspend fun getTodaysApod(): ApodResponse {
        return httpClient.get("$baseUrl/api/apod/today").body()
    }

    suspend fun getApodHistory(page: Int, pageSize: Int): ApiResult<PaginatedResponse<ApodResponse>> =
        safeRequest {
            httpClient.get("$baseUrl/api/apod/history") {
                parameter("page", page)
                parameter("pageSize", pageSize)
            }.body()
        }


    suspend fun getApodByDate(date: String): ApodResponse {
        return httpClient.get("$baseUrl/api/apod/date/$date").body()
    }
}