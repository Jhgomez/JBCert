package okik.tech.fullstack.data.network.restapi.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import okik.tech.fullstack.data.network.restapi.util.safeRequest

class ApodApiService(private val httpClient: HttpClient) {

    suspend fun getTodaysApod(): ApiResult<ApodResponse> = safeRequest {
        httpClient.get("api/apod/today").body()
    }

    suspend fun getApodHistory(page: Int, pageSize: Int): ApiResult<PaginatedResponse<ApodResponse>> =
        safeRequest {
            httpClient.get("api/apod/history") {
                parameter("page", page)
                parameter("pageSize", pageSize)
            }.body()
        }

    suspend fun getApodByDate(date: String): ApiResult<ApodResponse> = safeRequest {
        httpClient.get("api/apod/date/$date").body()
    }
}