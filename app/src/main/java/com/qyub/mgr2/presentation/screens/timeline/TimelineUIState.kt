package com.qyub.mgr2.presentation.screens.timeline

import androidx.compose.ui.graphics.Color
import com.qyub.mgr2.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

data class TimelineUIState(
    val displayDay: LocalDate = LocalDate.now(),
    val events: Map<LocalDate, List<EventUIState>> = emptyMap(),
    val inspectedEvent: EventUIState? = null,
)

data class EventUIState(
    val id: Int,
    val taskRef: Task.Scheduled,
    val color: Color,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val top: Int,
    val left: Float,
    val width: Float,
    val height: Int
)

sealed interface TimelineEvent {

}