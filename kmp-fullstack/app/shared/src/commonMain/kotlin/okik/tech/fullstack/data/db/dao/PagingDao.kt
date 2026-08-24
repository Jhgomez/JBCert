package okik.tech.fullstack.data.db.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.paging3.QueryPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.FullstackDb
import okik.tech.fullstack.db.PageInfoEntity

class PagingDao(val database: FullstackDb, val dispatcher: CoroutineDispatcher) {
    val queries = database.pagingQueries

    suspend fun selectAll(pageId: String): PageInfoEntity = queries.selectPageInfo(pageId).awaitAsOne()

    suspend fun upsertPage(page: PageInfoEntity): PageInfoEntity {
        queries.upsert(page)

        return page
    }

    suspend fun delete() = queries.delete()
}