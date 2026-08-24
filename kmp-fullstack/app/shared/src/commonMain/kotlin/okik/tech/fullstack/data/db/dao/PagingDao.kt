package okik.tech.fullstack.data.db.dao

import app.cash.sqldelight.async.coroutines.awaitAsOne
import okik.tech.fullstack.db.FullstackDb
import okik.tech.fullstack.db.PageInfoEntity

class PagingInfoDaoImpl(
    val database: FullstackDb
): PagingInfoDao {
    private val queries = database.pagingQueries

    override suspend fun select(id: String): PageInfoEntity = queries.selectPageInfo(id).awaitAsOne()

    override suspend fun upsertPage(entry: PageInfoEntity): PageInfoEntity {
        queries.updatePage(
            page = entry.page,
            name = entry.name
        )

        queries.insertPage(entry)

        return entry
    }

    override suspend fun delete() {
        queries.delete()
    }
}