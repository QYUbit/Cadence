package com.qyub.mgr2.presentation.screens.timeline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.qyub.mgr2.domain.model.Event
import java.time.LocalDate
import java.time.LocalTime

data class TimelineUIState(
    val displayDay: LocalDate = LocalDate.now(),
    val events: List<Event> = emptyList(),
    val eventItems: List<UIEvent> = emptyList()
)

data class UIEvent(
    val id: Int,
    val title: String,
    val color: Color,
    val startTime: LocalTime,
    val duration: Int,
    val top: Int,
    val left: Float,
    val width: Float,
    val height: Int
)

sealed interface TimelineEvent {

}