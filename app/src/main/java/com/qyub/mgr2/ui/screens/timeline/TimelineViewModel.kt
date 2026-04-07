package com.qyub.mgr2.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.data.repo.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val _displayDay = MutableStateFlow(LocalDate.now())
    val displayDay: StateFlow<LocalDate> = _displayDay.asStateFlow()

    private val eventCache = mutableMapOf<LocalDate, StateFlow<List<Event>>>()

    fun setDay(day: LocalDate) {
        _displayDay.value = day
    }

    fun eventsForDateFlow(date: LocalDate): StateFlow<List<Event>> =
        eventCache.getOrPut(date) {
            repo.eventsForDate(date)
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        }

    fun addEvent(event: Event) {
        viewModelScope.launch { repo.addEvent(event) }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch { repo.updateEvent(event) }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch { repo.deleteEvent(event) }
    }
}
