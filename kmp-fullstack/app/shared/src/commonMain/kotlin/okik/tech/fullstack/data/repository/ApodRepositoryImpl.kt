package okik.tech.fullstack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.Paging

class ApodRepositoryImpl(private val apiService: ApodApiService) : ApodRepository {

    override suspend fun getTodayApod(): DomainResult<Apod> =
        apiService
            .getTodaysApod()
            .toDomainModel(ApodResponse::toDomainModel)

    override suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>> =
        apiService
            .getApodHistory(page, pageSize)
            .toDomainModel { dataModel ->
                dataModel.toDomainModel(ApodResponse::toDomainModel)
            }


    override suspend fun getApodByDate(date: String): DomainResult<Apod> =
        apiService
            .getApodByDate(date)
            .toDomainModel(ApodResponse::toDomainModel)

    override fun getTodayApodFlow(): Flow<DomainResult<Apod>> = flow {
        emit(getTodayApod())
    }
}
