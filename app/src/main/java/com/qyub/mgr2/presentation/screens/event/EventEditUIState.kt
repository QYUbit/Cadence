package com.qyub.mgr2.presentation.screens.event

import androidx.compose.ui.graphics.Color
import com.qyub.mgr2.domain.model.Event
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.min

data class EventEditUIState (
    val id: Int? = null,
    val title: String = "",
    val color: String = "",
    val date: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.of(min(LocalTime.now().hour + 1, 23) , 0),
    val endTime: LocalTime = LocalTime.of(min(LocalTime.now().hour + 1, 23) , 30)
)

fun Event.toEventEditUIState() = EventEditUIState(
    id = id,
    title = title,
    color = "",
    date = date,
    startTime = startTime,
    endTime = startTime.plusMinutes(duration.toLong())
)

fun EventEditUIState.toEvent() = Event(
    id = 0,
    title = title,
    color = Color.Red,
    date = date,
    startTime = startTime,
    duration = Duration.between(startTime, endTime).toMinutes().toInt()
)
