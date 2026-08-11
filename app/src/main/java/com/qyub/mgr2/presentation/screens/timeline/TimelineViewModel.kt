package com.qyub.mgr2.presentation.screens.timeline

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.domain.repository.EventRepository
import com.qyub.mgr2.domain.usecase.GetEventsUseCase
import com.qyub.mgr2.domain.usecase.PreloadEventsUseCase
import com.qyub.mgr2.presentation.screens.timeline.lib.calculateEventDimensions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import java.time.Duration

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val preloadEventsUseCase: PreloadEventsUseCase,
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimelineUIState())
    val uiState: StateFlow<TimelineUIState> = _uiState.asStateFlow()

    init {
        loadEventItems()
    }

    private fun loadEventItems() {
        viewModelScope.launch {
            val eventItems = getEventsUseCase(_uiState.value.displayDay)
            _uiState.update {
                it.copy(events = calculateEventDimensions(eventItems))
            }
            preloadEventsUseCase(_uiState.value.displayDay.minusDays(1))
            preloadEventsUseCase(_uiState.value.displayDay.minusDays(2))
            preloadEventsUseCase(_uiState.value.displayDay.plusDays(1))
            preloadEventsUseCase(_uiState.value.displayDay.plusDays(2))
        }
    }

    fun setDay(day: LocalDate) {
        _uiState.update {
            it.copy(displayDay = day)
        }
        loadEventItems()
    }

    fun setInspectedEvent(event: EventUIState?) {
        _uiState.update {
            it.copy(inspectedEvent = event)
        }
    }

    fun onEventDelete(event: EventUIState) {
        viewModelScope.launch {
            eventRepository.deleteEvent(event.eventRef)
        }
    }
}