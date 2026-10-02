package com.qyub.mgr2.domain.repository

import com.qyub.mgr2.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getTaskById(id: Int): Flow<Task>
    suspend fun getEventsForDate(date: LocalDate): Flow<List<Task>>
    suspend fun preloadEventsForDate(date: LocalDate)
}