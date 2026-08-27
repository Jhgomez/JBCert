package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.Media
import okik.tech.fullstack.services.FileInfo
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert

class MediaDao {
    fun set(fileInfo: FileInfo): String {
        Media.upsert(Media.id) {
            it[Media.id] = fileInfo.url
            it[Media.mediaFile] = fileInfo.fileName
            it[Media.contentType] = fileInfo.contentType
        }
        
        return fileInfo.fileName
    }

    fun get(keyUrl: String): String? {
        return MediaEntity.Dao.findById(keyUrl)?.mediaFile
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
    var mediaFile by Media.mediaFile
    var contentType by Media.contentType

    override fun toString(): String {
        return "MediaDao(key=$key, pictureFile=${mediaFile}, contentType=$contentType)"
    }
}