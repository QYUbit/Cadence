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

        return eventsFlow.map { events ->
            val list = mutableListOf<Task.Scheduled>()

            events.forEach { event ->
                when (event) {
                    is Task.Scheduled ->
                        if (event.occurrence.appliesToDate(day)
                                && if (event.rangeStart != null) event.rangeStart <= day else true
                                && if (event.rangeEnd != null) event.rangeEnd >= day else true) {
                            list.add(event)
                        }

                    is Task.Unscheduled -> {}
                }
            }

            Log.i("GetEventsUseCase", "loading events for $day; found ${events.size} candidates, filtered ${list.size} final events")

            list
        }

        // TODO Cleaner code

        /*

        return eventsFlow.map { events ->
            events.filter { event ->
                when (event) {
                    is Task.Scheduled ->
                        event.occurrence.appliesToDate(day)
                            && if (event.rangeStart != null) event.rangeStart <= day else true
                                && if (event.rangeEnd != null) event.rangeEnd >= day else true

                    is Task.Unscheduled -> false
                }
            }
        }*/
    }
}