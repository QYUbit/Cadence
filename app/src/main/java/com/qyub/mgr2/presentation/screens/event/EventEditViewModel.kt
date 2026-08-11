package com.qyub.mgr2.presentation.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EventEditViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventEditUIState())
    val uiState: StateFlow<EventEditUIState> = _uiState.asStateFlow()

    fun loadEvent(id: Int) {
        viewModelScope.launch {
            val event = eventRepository.getEventById(id)
            _uiState.update { event.toEventEditUIState() }
        }
    }

    fun submitCreate() {
        viewModelScope.launch {
            eventRepository.insertEvent(_uiState.value.toEvent())
        }
    }

    fun submitEdit() {
        viewModelScope.launch {
            eventRepository.updateEvent(_uiState.value.toEvent())
        }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun setStartTime(startTime: LocalTime) {
        _uiState.update { it.copy(startTime = startTime) }
    }

    fun setEndTime(endTime: LocalTime) {
        _uiState.update { it.copy(endTime = endTime) }
    }
}