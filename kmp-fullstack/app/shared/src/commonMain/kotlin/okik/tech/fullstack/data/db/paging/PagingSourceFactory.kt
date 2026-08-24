package okik.tech.fullstack.data.db.paging

import app.cash.sqldelight.paging3.QueryPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.db.FullstackDb

class PagingSourceFactory(database: FullstackDb, private val dispatcher: CoroutineDispatcher) {
    val queries = database.apodQueries

    fun pagingSource() = QueryPagingSource(
        countQuery = queries.countApods(),
        transacter = queries,
        context = dispatcher,
        queryProvider = queries::apodsPage
    )
}