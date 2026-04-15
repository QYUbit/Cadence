package com.qyub.mgr2.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qyub.mgr2.data.models.Event

@Database(entities = [Event::class], version = 10)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_db")
                    .fallbackToDestructiveMigration(true) // dev only
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}