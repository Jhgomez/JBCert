package okik.tech.fullstack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import okik.tech.fullstack.data.db.ApodDao
import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import okik.tech.fullstack.data.repository.toDomainResultSuccess
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.models.PaginatedResponse
import kotlin.time.Instant

class ApodRepositoryImpl(
    private val apiService: ApodApiService,
    private val apodDao: ApodDao
) : ApodRepository {

    override suspend fun getTodayApod(): DomainResult<Apod> =
        apiService
            .getTodaysApod()
            .toDomainErrorModel(ApodResponse::toDomainErrorModel)

    override suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>> =
        when(val response = apiService.getApodHistory(page, pageSize)) {
            is ApiResult.Error<PaginatedResponse<ApodResponse>> -> response.toDomainResultError()
            is ApiResult.Success<PaginatedResponse<ApodResponse>> -> {

                val apodEntities = Array(response.result.items.size) { index ->
                    response.result.items[index].toApodEntity()
                }

                val insertedEntities = apodDao.upsert(apodEntities)

                DomainResult.Success(
                    result = Paging(
                        items = insertedEntities.map(ApodEntity::toDomainModel),
                        page = response.result.page,
                        pageSize = response.result.pageSize,
                        totalItems = response.result.totalItems,
                        totalPages = response.result.totalPages
                    )
                )
            }
        }


    override suspend fun getApodByDate(date: String): DomainResult<Apod> =
        apiService
            .getApodByDate(date)
            .toDomainErrorModel(ApodResponse::toDomainErrorModel)

    override fun getTodayApodFlow(): Flow<DomainResult<Apod>> = flow {
        emit(getTodayApod())
    }
}
