package com.qyub.mgr2.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qyub.mgr2.data.db.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: EventEntity)

    @Query("SELECT * FROM events WHERE date = :date")
    suspend fun getEventsForDate(date: Long): List<EventEntity>
}