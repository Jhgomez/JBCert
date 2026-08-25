package okik.tech.fullstack.data.db.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacter
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterBase
import app.cash.sqldelight.TransactionCallbacks
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

class AsyncOffsetQueryPagingSource<RowType : Any>(
    private val queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    private val countQuery: Query<Long>,
    private val transacter: SuspendingTransacter,
    private val context: CoroutineContext,
    private val initialOffset: Int = 0,
) : PagingSource<Int, RowType>(), Query.Listener {

    override val jumpingSupported get() = true

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, RowType> = withContext(context) {
        val key = params.key ?: initialOffset
        val limit = when (params) {
            is LoadParams.Prepend -> minOf(key, params.loadSize)
            else -> params.loadSize
        }
        val getPagingSourceLoadResult: suspend TransactionCallbacks.() -> LoadResult.Page<Int, RowType> = {
            val count = countQuery.awaitAsOne()
            val offset = when (params) {
                is LoadParams.Prepend -> maxOf(0, key - params.loadSize)
                is LoadParams.Append -> key
                is LoadParams.Refresh -> if (key >= count - params.loadSize) maxOf(0, count - params.loadSize).toInt() else key
            }
            val data = queryProvider(limit.toLong(), offset.toLong())
                .also { currentQuery = it }
                .awaitAsList()
            val nextPosToLoad = offset + data.size
            LoadResult.Page(
                data = data,
                prevKey = offset.takeIf { it > 0 && data.isNotEmpty() },
                nextKey = nextPosToLoad.takeIf { data.isNotEmpty() && data.size >= limit && it < count },
                itemsBefore = offset,
                itemsAfter = maxOf(0, count - nextPosToLoad).toInt(),
            )
        }

        val loadResult = (transacter as SuspendingTransacter).transactionWithResult(bodyWithReturn = getPagingSourceLoadResult)
        (if (invalid) LoadResult.Invalid() else loadResult)
    }

    override fun getRefreshKey(state: PagingState<Int, RowType>) = state.anchorPosition?.let { maxOf(0, it - (state.config.initialLoadSize / 2)) }

    protected var currentQuery: Query<RowType>? by Delegates.observable(null) { _, old, new ->
        old?.removeListener(this)
        new?.addListener(this)
    }

    init {
        registerInvalidatedCallback {
            currentQuery?.removeListener(this)
            currentQuery = null
        }
    }

    override fun queryResultsChanged() = invalidate()
}