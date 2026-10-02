package com.qyub.mgr2.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qyub.mgr2.data.db.dao.TaskDao
import com.qyub.mgr2.data.db.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class
    ],
    version = 12,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}