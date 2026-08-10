package com.qyub.mgr2.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qyub.mgr2.data.db.dao.EventDao
import com.qyub.mgr2.data.db.entity.EventEntity

@Database(
    entities = [
        EventEntity::class
    ],
    version = 11,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}