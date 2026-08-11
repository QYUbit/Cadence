package com.qyub.mgr2.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qyub.mgr2.data.db.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: EventEntity)
    @Update
    suspend fun update(event: EventEntity)
    @Delete
    suspend fun delete(event: EventEntity)
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): EventEntity

    @Query("SELECT * FROM events WHERE date = :date")
    suspend fun getEventsForDate(date: Long): List<EventEntity>
}