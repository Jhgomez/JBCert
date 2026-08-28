package okik.tech.fullstack.feature.home.impl.apodlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import fullstack.app.shared.generated.resources.Res
import fullstack.app.shared.generated.resources.arrow_right
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.DomainResult
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeListScreen(
    onApodClick: (Apod, MemoryCache.Key?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeListViewModel = koinViewModel()
) {
    val state = viewModel.apodPagesFlow.collectAsLazyPagingItems()

    val mediatorLoadState = state.loadState.mediator?.refresh

    val errorResult = if (mediatorLoadState is LoadState.Error) {
        mediatorLoadState.error as DomainResult.DomainErrorResult
    } else {
        null
    }

    val showAppendLoading = state.loadState.append == LoadState.Loading
    val isRefreshInProgress = state.loadState.refresh == LoadState.Loading

    HomeListScreen(
        apods = state.itemSnapshotList,
        apodCallback = { index -> state[index] },
        isRefreshInProgress = isRefreshInProgress,
        showAppendLoading = showAppendLoading,
        errorResult = errorResult,
        modifier = modifier,
        onApodClick = onApodClick
    )
}

@Composable
fun HomeListScreen(
    apods: List<Apod?>,
    apodCallback: (Int) -> Apod?,
    isRefreshInProgress: Boolean,
    showAppendLoading: Boolean,
    errorResult: DomainResult.DomainErrorResult?,
    modifier: Modifier = Modifier,
    onApodClick: (Apod, MemoryCache.Key?) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 24.dp,
            end = 8.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            count = (apods.size - 1).coerceAtLeast(0),
            key = { index ->
//                if (apods.size < index) {
                    apods[index]?.id ?: -index.toLong()
//                } else {
//                    -index.toLong()
//                }
            }
        ) { index ->
            ApodItem(onApodClick, apodCallback(index))
        }
    }
}

@Composable
private fun ApodItem(
    onApodClick: (Apod, MemoryCache.Key?) -> Unit,
    apod: Apod?
) {
    var placeholder: MemoryCache.Key? = remember { null }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = apod != null) {
                onApodClick(apod!!, placeholder)
            },
        headlineContent = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = apod?.title ?: " ",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        if (apod != null)
                            Modifier
                        else
                            Modifier
                                .fillMaxWidth(.6f)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.secondaryFixedDim)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = apod?.explanation ?: " \n ",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        if (apod != null)
                            Modifier
                        else
                            Modifier
                                .fillMaxWidth(.9f)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.secondaryFixedDim)
                )
            }
        },
        leadingContent = {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(apod?.url)
                    .build(),
                contentDescription = null,
                onSuccess = { placeholder = it.result.memoryCacheKey },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        trailingContent = {
            Icon(
                imageVector = vectorResource(Res.drawable.arrow_right),
                contentDescription = null
            )
        }
    )
}

