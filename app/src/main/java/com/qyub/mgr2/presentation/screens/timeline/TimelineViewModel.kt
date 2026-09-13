package com.qyub.mgr2.presentation.screens.timeline

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.domain.repository.EventRepository
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
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _displayDay = MutableStateFlow(LocalDate.now())
    private val _inspectedEvent = MutableStateFlow<EventUIState?>(null)

    private val _events: Flow<List<EventUIState>> = _displayDay.flatMapLatest { day ->
        preloadEvents(day)
        val days = listOf(day.minusDays(1), day, day.plusDays(1))
        combine(
            days.map { d ->
                getEventsUseCase(d).map { calculateEventDimensions(it) }
            }
        ) { results ->
            results.flatMap { it }
        }
    }

    val uiState: StateFlow<TimelineUIState> = combine(
        _displayDay,
        _inspectedEvent,
        _events
    ) { day, inspected, events ->
        TimelineUIState(
            displayDay = day,
            events = events,
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
            eventRepository.deleteEvent(event.eventRef)
        }
    }
}