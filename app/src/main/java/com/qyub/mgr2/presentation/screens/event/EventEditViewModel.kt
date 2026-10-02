package com.qyub.mgr2.presentation.screens.event

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class EventEditViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventEditUIState())
    val uiState: StateFlow<EventEditUIState> = _uiState.asStateFlow()

    private var loadedEventId: Int? = null

    fun loadEvent(id: Int) {
        if (loadedEventId == id) return
        viewModelScope.launch {
            val task = taskRepository.getTaskById(id).first()
            if (task is Task.Scheduled) {
                _uiState.update { task.toEventEditUIState() }
            }
            loadedEventId = id
        }
    }

    fun resetState() {
        loadedEventId = null
        _uiState.update { EventEditUIState() }
    }

    suspend fun submitCreate() {
        taskRepository.insertTask(_uiState.value.toTask())
    }

    suspend fun submitEdit() {
        taskRepository.updateTask(_uiState.value.toTask())
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setNotes(notes: String?) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun setColor(color: Color) {
        _uiState.update { it.copy(color = color) }
    }

    fun setOccurrence(occurrence: Occurrence) {
        _uiState.update {
            val baseDate = when (occurrence) {
                is Occurrence.FixedDate -> occurrence.date
                else -> it.date
            }
            it.copy(occurrence = occurrence, date = baseDate)
        }
    }

    fun setAllDay(isAllDay: Boolean) {
        _uiState.update {
            it.copy(
                isAllDay = isAllDay,
                startTime = if (isAllDay) null else (it.startTime ?: LocalTime.now())
            )
        }
    }

    fun setStartTime(startTime: LocalTime?) {
        _uiState.update { it.copy(startTime = startTime) }
    }

    fun setDurationMinutes(minutes: Long) {
        _uiState.update { it.copy(duration = Duration.ofMinutes(minutes.coerceAtLeast(1))) }
    }
}
