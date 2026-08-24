package okik.tech.fullstack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okik.tech.fullstack.data.db.dao.ApodDao
import okik.tech.fullstack.data.db.dao.PagingInfoDao
import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import okik.tech.fullstack.data.repository.mapper.toApodEntity
import okik.tech.fullstack.data.repository.mapper.toDomainModel
import okik.tech.fullstack.data.repository.mapper.toDomainResultError
import okik.tech.fullstack.data.repository.mapper.toEntity
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.PageInfo
import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.models.PaginatedResponse

class ApodRepositoryImpl(
    private val apiService: ApodApiService,
    private val apodDao: ApodDao,
    private val pagingInfoDao: PagingInfoDao
) : ApodRepository {

    override suspend fun getTodayApod(): DomainResult<Apod> =
        when (val response = apiService.getTodaysApod()) {
            is ApiResult.Error<ApodResponse> -> response.toDomainResultError()
            is ApiResult.Success<ApodResponse> -> {
                val insertedApod = apodDao.upsertApod(response.result.toApodEntity()).toDomainModel()

                DomainResult.Success(
                    result = insertedApod
                )
            }
        }

    override suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>> =
        when(val response = apiService.getApodHistory(page, pageSize)) {
            is ApiResult.Error<PaginatedResponse<ApodResponse>> -> response.toDomainResultError()
            is ApiResult.Success<PaginatedResponse<ApodResponse>> -> {

                val apodEntities = Array(response.result.items.size) { index ->
                    response.result.items[index].toApodEntity()
                }

                val insertedEntities = apodDao.upsertApods(apodEntities)

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
        when (val response = apiService.getApodByDate(date)) {
            is ApiResult.Error<ApodResponse> -> response.toDomainResultError()
            is ApiResult.Success<ApodResponse> -> {
                val insertedApod = apodDao.upsertApod(response.result.toApodEntity()).toDomainModel()

                DomainResult.Success(
                    result = insertedApod
                )
            }
        }

    override fun getTodayApodFlow(): Flow<DomainResult<Apod>> = flow {
        emit(getTodayApod())
    }

    override suspend fun upsertPageInfo(pageInfo: PageInfo): PageInfo {
        pagingInfoDao.upsertPage(pageInfo.toEntity())

        return pageInfo
    }

    override suspend fun deletePagesInfo() {
        pagingInfoDao.delete()
    }

    override suspend fun getPageInfo(id: String): PageInfo =
        pagingInfoDao.select(id).toDomainModel()

}
