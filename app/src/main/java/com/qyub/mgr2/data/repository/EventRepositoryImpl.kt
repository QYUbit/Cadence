package com.qyub.mgr2.data.repository

import com.qyub.mgr2.data.db.dao.TaskDao
import com.qyub.mgr2.data.db.entity.toDomain
import com.qyub.mgr2.data.db.entity.toEntity
import com.qyub.mgr2.domain.model.EventExceptionInfo
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    // eventCache only contains event candidates
    val eventCache = ConcurrentHashMap<LocalDate, Flow<List<Task>>>()

    override suspend fun insertTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun cancelOccurrence(parentTaskId: Int, originalDate: LocalDate) {
        val parentEntity = taskDao.getEventById(parentTaskId).firstOrNull()
        val canceledTask = Task.Scheduled(
            id = 0,
            title = parentEntity?.title ?: "",
            notes = null,
            color = parentEntity?.color ?: 0,
            duration = null,
            occurrence = Occurrence.FixedDate(originalDate),
            rangeStart = originalDate,
            rangeEnd = originalDate,
            startTime = null,
            exceptionInfo = EventExceptionInfo(
                exceptionParentId = parentTaskId,
                originalDate = originalDate,
                isCanceled = true
            )
        )
        taskDao.insert(canceledTask.toEntity())
    }

    override suspend fun deleteTaskSeries(parentTaskId: Int) {
        taskDao.deleteSeries(parentTaskId)
    }

    override suspend fun getTaskById(id: Int): Flow<Task> {
        return taskDao.getEventById(id).map { it.toDomain() }
    }

    override suspend fun getEventsForDate(date: LocalDate): Flow<List<Task>> {
        return eventCache.getOrPut(date) {
            taskDao.getEventsForDate(date.toEpochDay()).map { events ->
                events.map { item ->
                    item.toDomain()
                }
            }
        }
    }

    override suspend fun preloadEventsForDate(date: LocalDate) {
        eventCache.putIfAbsent(
            date,
            taskDao.getEventsForDate(date.toEpochDay()).map { events ->
                events.map { item ->
                    item.toDomain()
                }
            }
        )
    }
}