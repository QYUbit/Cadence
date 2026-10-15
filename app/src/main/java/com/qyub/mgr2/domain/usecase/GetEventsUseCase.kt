package com.qyub.mgr2.domain.usecase

import android.util.Log
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.model.appliesToDate
import com.qyub.mgr2.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(day: LocalDate): Flow<List<Task.Scheduled>> {
        val eventsFlow = taskRepository.getEventsForDate(day)

        return eventsFlow.map { tasks ->
            val scheduledTasks = tasks.filterIsInstance<Task.Scheduled>()

            val baseTasks = scheduledTasks.filter { it.exceptionInfo == null }
            val exceptions = scheduledTasks.filter { it.exceptionInfo != null }

            val exceptionsForDayByParentId = exceptions
                .filter { it.exceptionInfo?.originalDate == day }
                .associateBy { it.exceptionInfo!!.exceptionParentId }

            val resultList = mutableListOf<Task.Scheduled>()

            baseTasks.forEach { task ->
                val inRange = (task.rangeStart == null || task.rangeStart <= day) &&
                        (task.rangeEnd == null || task.rangeEnd >= day)

                if (inRange && task.occurrence.appliesToDate(day)) {
                    val matchingException = exceptionsForDayByParentId[task.id]
                    if (matchingException != null) {
                        if (!matchingException.exceptionInfo!!.isCanceled) {
                            resultList.add(matchingException)
                        }
                    } else {
                        resultList.add(task)
                    }
                }
            }

            exceptions.forEach { exc ->
                val excInfo = exc.exceptionInfo!!
                if (!excInfo.isCanceled && excInfo.originalDate != day && exc.occurrence.appliesToDate(day)) {
                    resultList.add(exc)
                }
            }

            Log.i("GetEventsUseCase", "loading events for $day; candidates ${tasks.size}, result ${resultList.size}")

            resultList
        }
    }
}