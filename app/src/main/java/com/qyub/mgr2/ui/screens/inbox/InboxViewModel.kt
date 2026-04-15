package com.qyub.mgr2.ui.screens.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.data.repo.EventRepository
import com.qyub.mgr2.ui.components.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        viewModelScope.launch {
            repo.inboxEvents().collect { events ->
                _events.value = events
            }
        }
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
