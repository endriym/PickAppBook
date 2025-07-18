package com.munity.pickappbook.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.munity.pickappbook.core.data.local.database.dao.FavoritePickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.FeedPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PersonalPickupLineWithTagsDao
import com.munity.pickappbook.core.data.local.database.dao.PickupLineDao
import com.munity.pickappbook.core.data.local.database.dao.TagDao
import com.munity.pickappbook.core.data.local.database.entity.FavoritePLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.FeedPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PersonalPLTagCrossRefEntity
import com.munity.pickappbook.core.data.local.database.entity.PickupLineEntity
import com.munity.pickappbook.core.data.local.database.entity.TagEntity

@Database(
    entities = [PickupLineEntity::class, TagEntity::class, FeedPLTagCrossRefEntity::class, PersonalPLTagCrossRefEntity::class, FavoritePLTagCrossRefEntity::class],
    version = 1
)
abstract class PickAppDatabase : RoomDatabase() {
    abstract val pickupLineDao: PickupLineDao
    abstract val tagDao: TagDao
    abstract val feedPickupLineWithTagsDao: FeedPickupLineWithTagsDao
    abstract val personalPickupLineWithTagsDao: PersonalPickupLineWithTagsDao
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
