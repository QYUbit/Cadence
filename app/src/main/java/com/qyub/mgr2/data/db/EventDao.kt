package com.qyub.mgr2.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.qyub.mgr2.data.models.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun allEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE date BETWEEN :start AND :end ORDER BY date")
    fun eventsBetween(start: Long, end: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isRepeating = 1 OR date = :targetDate")
    fun getEventsForDate(targetDate: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE repeatFor = 'NONE'")
    fun getInboxEvents(): Flow<List<Event>>

    // I know that upsert exists
    @Insert
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}