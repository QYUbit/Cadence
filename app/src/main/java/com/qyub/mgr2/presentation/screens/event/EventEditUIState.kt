package com.qyub.mgr2.presentation.screens.event

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.qyub.mgr2.domain.model.EventExceptionInfo
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class EventEditUIState(
    val id: Int? = null,
    val title: String = "",
    val notes: String? = null,
    val color: Color = Color.Gray,
    val date: LocalDate = LocalDate.now(),
    val isAllDay: Boolean = false,
    val startTime: LocalTime? = null,
    val duration: Duration? = Duration.ofMinutes(30),
    val occurrence: Occurrence = Occurrence.FixedDate(LocalDate.now()),
    val rangeStart: LocalDate? = null,
    val rangeEnd: LocalDate? = null,
    val exceptionInfo: EventExceptionInfo? = null,
    val originalDate: LocalDate = LocalDate.now(),
    val isRepeatingSeries: Boolean = false
)

fun Task.Scheduled.toEventEditUIState(targetDate: LocalDate? = null): EventEditUIState {
    val baseDate = targetDate ?: when (val occ = occurrence) {
        is Occurrence.FixedDate -> occ.date
        else -> rangeStart ?: LocalDate.now()
    }
    val isRepeating = occurrence !is Occurrence.FixedDate && exceptionInfo == null

    return EventEditUIState(
        id = id,
        title = title,
        notes = notes,
        color = Color(color),
        date = baseDate,
        isAllDay = startTime == null,
        startTime = startTime,
        duration = duration ?: Duration.ofMinutes(30),
        occurrence = occurrence,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd,
        exceptionInfo = exceptionInfo,
        originalDate = baseDate,
        isRepeatingSeries = isRepeating
    )
}

fun EventEditUIState.toTask(): Task.Scheduled {
    val resolvedOccurrence = when (occurrence) {
        is Occurrence.FixedDate -> Occurrence.FixedDate(date)
        else -> occurrence
    }
    return Task.Scheduled(
        id = id ?: 0,
        title = title,
        notes = notes,
        color = color.toArgb(),
        duration = duration,
        occurrence = resolvedOccurrence,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd,
        startTime = if (isAllDay) null else startTime,
        exceptionInfo = exceptionInfo
    )
}
