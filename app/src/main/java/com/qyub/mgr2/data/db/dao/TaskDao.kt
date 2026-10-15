package com.qyub.mgr2.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qyub.mgr2.data.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: TaskEntity)
    @Update
    suspend fun update(event: TaskEntity)
    @Delete
    suspend fun delete(event: TaskEntity)
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getEventById(id: Int): Flow<TaskEntity>

    @Query("SELECT * FROM tasks WHERE fixedDate = :date OR originalDate = :date OR (occurrenceType <> 'FixedDate' AND occurrenceType IS NOT NULL AND exceptionParentId IS NULL)")
    fun getEventsForDate(date: Long): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE id = :parentId OR exceptionParentId = :parentId")
    suspend fun deleteSeries(parentId: Int)
}