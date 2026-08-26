package okik.tech.fullstack.data.db.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.Paging

@OptIn(ExperimentalPagingApi::class)
class ApodHistoryRemoteMediator(val apodRepository: ApodRepository): RemoteMediator<Int, ApodEntity>() {
    val HIGHEST_PAGE_INDEX = "highest_page"

    var currentIndex = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ApodEntity>
    ): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> currentIndex = 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    ++currentIndex

                    if (currentIndex > apodRepository.getPageInfo(HIGHEST_PAGE_INDEX).page) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }
            }

            val response = apodRepository.getApodHistory(
                page = currentIndex,
                pageSize = state.config.pageSize
            )

            if (response is DomainResult.Success<Paging<Apod>>) {
                if (loadType == LoadType.REFRESH) {
                    // TODO check for internet connection before accepting refresh to avoid emptying DB
                    apodRepository.refreshApodAndPagingInfo(
                        page = response.result,
                        highestPageIndex = HIGHEST_PAGE_INDEX
                    )
                } else { // else is always APPEND
                    apodRepository.upsertApods(response.result.items)
                }
            } else {
                return MediatorResult.Error(
                    response as DomainResult.DomainErrorResult
                )
            }

            // if we get here it is because it is false
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}