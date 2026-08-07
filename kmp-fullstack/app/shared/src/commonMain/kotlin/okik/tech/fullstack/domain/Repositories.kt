package okik.tech.fullstack.domain

import kotlinx.coroutines.flow.Flow

interface ApodRepository {
    suspend fun getTodayApod(): DomainResult<Apod>
    suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>>
    suspend fun getApodByDate(date: String): DomainResult<Apod>
    fun getTodayApodFlow(): Flow<DomainResult<Apod>>
}