package okik.tech.fullstack.domain

import kotlinx.coroutines.flow.Flow

interface ApodRepository {
    suspend fun getTodayApod(): DomainResult<Apod>
    suspend fun getApodHistory(page: Int, pageSize: Int): DomainResult<Paging<Apod>>
    suspend fun getApodByDate(date: String): DomainResult<Apod>
    fun getTodayApodFlow(): Flow<DomainResult<Apod>>
    suspend fun upsertPageInfo(pageInfo: PageInfo): PageInfo
    suspend fun deletePagesInfo()
    suspend fun getPageInfo(id: String): PageInfo
    suspend fun refreshApodAndPagingInfo(
        page: Paging<Apod>,
        highestPageIndex: String
    )
    suspend fun upsertApods(apods: List<Apod>)

    /**
     * This is returning Flow<PagingData<Apod>> but to avoid adding a UI dependency to domain layer
     * we return it as Any, and we are returning the paging data flow from this layer to avoid giving
     * the presentation layer access to the whole database just to be able to parse the entity into
     * a domain model(paging DataSource returns an entity), this happens only with sqldelight because
     * we don't have much control over the generated API, if we where using Room we could trade off
     * having a dependency on a module that only has access to the entity we need but not the whole
     * db nor any DAO
     */
    fun getApodPagingFlow(): Flow<Any>
}