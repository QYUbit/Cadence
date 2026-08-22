package com.qyub.mgr2.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qyub.mgr2.data.db.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: EventEntity)
    @Update
    suspend fun update(event: EventEntity)
    @Delete
    suspend fun delete(event: EventEntity)
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Flow<EventEntity>

    @Query("SELECT * FROM events WHERE date = :date")
    fun getEventsForDate(date: Long): Flow<List<EventEntity>>
}