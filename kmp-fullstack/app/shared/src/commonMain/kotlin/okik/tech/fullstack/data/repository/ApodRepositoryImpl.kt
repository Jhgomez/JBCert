package okik.tech.fullstack.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import okik.tech.fullstack.data.db.DbTransaction
import okik.tech.fullstack.data.db.dao.ApodDao
import okik.tech.fullstack.data.db.dao.PagingInfoDao
import okik.tech.fullstack.data.network.ApiResult
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import okik.tech.fullstack.data.repository.mapper.toApodEntity
import okik.tech.fullstack.data.repository.mapper.toDomainModel
import okik.tech.fullstack.data.repository.mapper.toDomainResultError
import okik.tech.fullstack.data.repository.mapper.toEntity
import okik.tech.fullstack.db.PageInfoEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.PageInfo
import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.data.db.paging.ApodHistoryRemoteMediator
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.models.PaginatedResponse
import kotlin.time.Clock

class ApodRepositoryImpl(
    private val apiService: ApodApiService,
    private val apodDao: ApodDao,
    private val pagingInfoDao: PagingInfoDao,
    private val dbTransaction: DbTransaction
) : ApodRepository {

    override suspend fun getTodayApod(): DomainResult<Apod> {
        val cacheTodayApod =
            apodDao.selectById(Clock.System.todayIn(TimeZone.UTC).toEpochDays())

        if (cacheTodayApod != null) return DomainResult.Success(
            result = cacheTodayApod.toDomainModel()
        )

        return when (val response = apiService.getTodaysApod()) {
            is ApiResult.Error<ApodResponse> -> response.toDomainResultError()
            is ApiResult.Success<ApodResponse> -> {
                val insertedApod =
                    apodDao.upsertApod(response.result.toApodEntity()).toDomainModel()

                DomainResult.Success(
                    result = insertedApod
                )
            }
        }
    }

    override suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>> =
        when(val response = apiService.getApodHistory(page, pageSize)) {
            is ApiResult.Error<PaginatedResponse<ApodResponse>> -> response.toDomainResultError()
            is ApiResult.Success<PaginatedResponse<ApodResponse>> -> {

                DomainResult.Success(
                    result = Paging(
                        items = response.result.items.map(ApodResponse::toDomainModel),
                        page = response.result.page,
                        pageSize = response.result.pageSize,
                        totalItems = response.result.totalItems,
                        totalPages = response.result.totalPages
                    )
                )
            }
        }

    override suspend fun getApodByDate(date: LocalDate): DomainResult<Apod> {
        val cacheTodayApod =
            apodDao.selectById(date.toEpochDays())

        if (cacheTodayApod != null) return DomainResult.Success(
            result = cacheTodayApod.toDomainModel()
        )

        return when (val response = apiService.getApodByDate(date.toString())) {
            is ApiResult.Error<ApodResponse> -> response.toDomainResultError()
            is ApiResult.Success<ApodResponse> -> {
                val insertedApod =
                    apodDao.upsertApod(response.result.toApodEntity()).toDomainModel()

                DomainResult.Success(
                    result = insertedApod
                )
            }
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

    override suspend fun refreshApodAndPagingInfo(
        page: Paging<Apod>,
        highestPageIndex: String
    ) {
        dbTransaction.transaction {
            apodDao.deleteAll()
            pagingInfoDao.delete()

            apodDao.upsertApods(
                Array(page.items.size) { index ->
                    page.items[index].toEntity()
                }
            )

            pagingInfoDao.upsertPage(
                PageInfoEntity(highestPageIndex, page.totalPages.toLong())
            )
        }
    }

    override suspend fun upsertApods(apods: List<Apod>) {
        dbTransaction.transaction {
            apodDao.upsertApods(
                Array(apods.size) { index ->
                    apods[index].toEntity()
                }
            )
        }
    }

    var pager: Pager<Int, ApodEntity>? = null

    override fun getApodPagingFlow(): Flow<Any> {
        @OptIn(ExperimentalPagingApi::class)
        val flow = pager?.flow ?:
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2, // pager config is really "fragile", if I put 2 here, it will fetch all pages of **this** backend as it was a loop, so it fetches all until they are all in memory
                    enablePlaceholders = true,
                    initialLoadSize = 10,
                ),
                pagingSourceFactory = {
                    apodDao.getApodPagingSource()
                },
                remoteMediator = ApodHistoryRemoteMediator(apodRepository = this)
            ).also {
                pager = it
            }
            .flow

        return flow.map { pagingData ->
                pagingData.map { apodEntity -> apodEntity.toDomainModel() }
            }
    }

    override fun appendPagerPage() {
        pager?.append()
    }
}
