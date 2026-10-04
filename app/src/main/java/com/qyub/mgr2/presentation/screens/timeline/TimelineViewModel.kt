package com.qyub.mgr2.presentation.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.repository.TaskRepository
import com.qyub.mgr2.domain.usecase.GetEventsUseCase
import com.qyub.mgr2.domain.usecase.PreloadEventsUseCase
import com.qyub.mgr2.presentation.screens.timeline.lib.calculateEventDimensions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val preloadEventsUseCase: PreloadEventsUseCase,
    private val eventRepository: TaskRepository,
) : ViewModel() {

    private val _displayDay = MutableStateFlow(LocalDate.now())
    private val _inspectedEvent = MutableStateFlow<EventUIState?>(null)

    private val _dayEventsMap: Flow<Map<LocalDate, Pair<List<EventUIState>, List<Task.Scheduled>>>> = _displayDay.flatMapLatest { day ->
        preloadEvents(day)
        val days = listOf(day.minusDays(1), day, day.plusDays(1))
        combine(
            days.map { d ->
                getEventsUseCase(d).map { tasks ->
                    val timedTasks = tasks.filter { it.startTime != null }
                    val allDayTasks = tasks.filter { it.startTime == null }
                    d to (calculateEventDimensions(timedTasks) to allDayTasks)
                }
            }
        ) { results ->
            results.toMap()
        }
    }

    val uiState: StateFlow<TimelineUIState> = combine(
        _displayDay,
        _inspectedEvent,
        _dayEventsMap
    ) { day, inspected, dayEventsMap ->
        TimelineUIState(
            displayDay = day,
            events = dayEventsMap.mapValues { it.value.first },
            allDayEvents = dayEventsMap.mapValues { it.value.second },
            inspectedEvent = inspected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimelineUIState()
    )

    private fun preloadEvents(day: LocalDate) {
        viewModelScope.launch {
            preloadEventsUseCase(day.minusDays(1))
            preloadEventsUseCase(day.minusDays(2))
            preloadEventsUseCase(day.plusDays(1))
            preloadEventsUseCase(day.plusDays(2))
        }
    }

    fun setDay(day: LocalDate) {
        _displayDay.value = day
    }

    fun setInspectedEvent(event: EventUIState?) {
        _inspectedEvent.value = event
    }

    fun onEventDelete(event: EventUIState) {
        viewModelScope.launch {
            eventRepository.deleteTask(event.taskRef)
        }
    }
}