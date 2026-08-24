package okik.tech.fullstack.data.repository.mapper

import okik.tech.fullstack.db.PageInfoEntity
import okik.tech.fullstack.domain.PageInfo

fun PageInfo.toEntity(): PageInfoEntity = PageInfoEntity(
    name = name,
    page = page.toLong()
)

fun PageInfoEntity.toDomainModel() = PageInfo(
    name = name,
    page = page.toInt()
)