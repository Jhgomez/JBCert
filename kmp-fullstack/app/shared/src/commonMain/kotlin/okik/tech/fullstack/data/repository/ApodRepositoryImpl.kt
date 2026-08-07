package okik.tech.fullstack.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import okik.tech.fullstack.network.restapi.ApiResult
import okik.tech.fullstack.network.restapi.services.ApodApiService

interface ApodRepository {
    suspend fun getTodayApod(): Result<ApodResponse>
    suspend fun getApodHistory(page: Int, pageSize: Int): Result<PaginatedResponse<ApodResponse>>
    suspend fun getApodByDate(date: String): Result<ApodResponse>
    fun getTodayApodFlow(): Flow<Result<ApodResponse>>
}

class ApodRepositoryImpl(
    private val apiService: ApodApiService
) : ApodRepository {

    override suspend fun getTodayApod(): Result<ApodResponse> {
        return try {
            val response = apiService.getTodaysApod()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApodHistory(page: Int, pageSize: Int): Result<PaginatedResponse<ApodResponse>> {
        try {
            when(val response = apiService.getApodHistory(page, pageSize)) {
                is ApiResult.Success<PaginatedResponse<ApodResponse>> -> TODO()
                is ApiResult.TimeOut<*> -> TODO()
                is ApiResult.NotFound<*> -> TODO()
                is ApiResult.Unauthorized<*> -> TODO()
                is ApiResult.UnknownResult<*> -> TODO()
            }
//            Result.success(response)
        } catch (e: Exception) {
//            Result.failure(e)
        }
    }

    override suspend fun getApodByDate(date: String): Result<ApodResponse> {
        return try {
            val response = apiService.getApodByDate(date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTodayApodFlow(): Flow<Result<ApodResponse>> = flow {
        emit(getTodayApod())
    }
}

// this class should implement a class for whatever codes you expect to receive from your backend
//sealed interface ApiResult<T> {
//    data class Success<R>(val data: R): ApiResult<R>
//    data class NotFound(val message: String): ApiResult<Unit>
//    data class Unauthorized(val message: String): ApiResult<Unit>
//}