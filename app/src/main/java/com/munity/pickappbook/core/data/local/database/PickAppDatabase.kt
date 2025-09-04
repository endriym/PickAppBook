package com.munity.pickappbook.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.munity.pickappbook.core.data.local.database.dao.FavoritePickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.FeedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PostedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.TagDao
import com.munity.pickappbook.core.data.local.database.entity.FavoritePickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.FeedPickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.PLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PostedPickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.TagEntity

@Database(
    entities = [PickupLineEntity::class, TagEntity::class, PLTagCrossRefEntity::class, FeedPickupLineEntity::class, PostedPickupLineEntity::class, FavoritePickupLineEntity::class],
    version = 1
)
abstract class PickAppDatabase : RoomDatabase() {
    abstract val pickupLineDao: PickupLineDao
    abstract val tagDao: TagDao
    abstract val pickupLineWithTagsDao: PickupLineWithTagsDao
    abstract val feedPickupLineWithTagsDao: FeedPickupLineWithTagsDao
    abstract val postedPickupLineWithTagsDao: PostedPickupLineWithTagsDao
    abstract val favoritePickupLineWithTagsDao: FavoritePickupLineWithTagsDao

    companion object {
        @Volatile
        private var Instance: PickAppDatabase? = null

        fun getDatabase(context: Context): PickAppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = PickAppDatabase::class.java,
                    name = "pickapp_database"
                ).fallbackToDestructiveMigration(true)
                    .build().also { Instance = it }
            }
        }
    }
}
