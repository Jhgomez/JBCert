package okik.tech.fullstack.data.db.dao

import androidx.paging.PagingSource
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.PageInfoEntity

interface ApodDao {
    fun getApodPagingSource(): PagingSource<Int, ApodEntity>
    suspend fun selectAll(): List<ApodEntity>
    suspend fun upsertApods(apods: Array<ApodEntity>): Array<ApodEntity>
    suspend fun upsertApod(apod: ApodEntity): ApodEntity
    suspend fun deleteAll()
}

interface PagingInfoDao {
    suspend fun select(id: String): PageInfoEntity
    suspend fun upsertPage(entry: PageInfoEntity): PageInfoEntity
    suspend fun delete()
}

