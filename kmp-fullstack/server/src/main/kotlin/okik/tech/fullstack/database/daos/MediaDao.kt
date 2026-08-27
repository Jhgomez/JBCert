package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.Media
import okik.tech.fullstack.services.FileInfo
import okik.tech.fullstack.utils.dbQuery
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.io.path.Path

class MediaDao {
    suspend fun set(fileInfo: FileInfo) = dbQuery {
        Media.upsert(Media.id) {
            it[Media.id] = fileInfo.url
            it[Media.mediaPath] = fileInfo.path.toString()
            it[Media.contentType] = fileInfo.contentType
        }
    }

    suspend fun get(keyUrl: String): FileInfo? = dbQuery {
        MediaEntity.Dao.findById(keyUrl)?.toFileInfo()
    }
}

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class MediaEntity(id: EntityID<String>) : Entity<String>(id) {
    object Dao : EntityClass<String, MediaEntity>(Media)
    var key by Media.id
    var mediaPath by Media.mediaPath
    var contentType by Media.contentType

    inline fun toFileInfo(): FileInfo = FileInfo(
        url = key.toString(),
        path = Path(mediaPath),
        contentType = contentType
    )

    override fun toString(): String {
        return "MediaDao(key=$key, pictureFile=${mediaPath}, contentType=$contentType)"
    }
}