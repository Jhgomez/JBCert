package okik.tech.fullstack.feature.home.impl.apodlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository

/**
 * This is a very simple screen there is no need to create a UiState class, tbe state is all
 * provided/driven by the PagingData Flow, the PagingState success is always converted into a flow
 * of type domain model APOD, and errors from network are translated into error of domain layer that
 * are then passed, again, by Paging data, the errors from the domain model extend from exception
 * class so that we can wrap them around MediatorResult.Error class, and all of this has to be handled
 */
class HomeListViewModel(private val apodRepository: ApodRepository): ViewModel() {

    // this is the paying price(unchecked cast) for following CA while leveraging androidx paging3 pager
    val apodPagesFlow: Flow<PagingData<Apod>> =
        (apodRepository.getApodPagingFlow() as Flow<PagingData<Apod>>).cachedIn(viewModelScope)

    fun refreshPagerData() {
//        apodRepository.refreshPagerData()
    }

    fun append() {
        apodRepository.appendPagerPage()
    }
}