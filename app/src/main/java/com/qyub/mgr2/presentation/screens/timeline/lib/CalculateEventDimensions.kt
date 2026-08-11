package com.qyub.mgr2.presentation.screens.timeline.lib

import com.qyub.mgr2.domain.model.Event
import com.qyub.mgr2.presentation.screens.timeline.EventUIState
import kotlin.math.max
import kotlin.math.min

fun calculateEventDimensions(events: List<Event>): List<EventUIState> {
    val minDuration = 10
    val minutesInDay = 24 * 60

    data class Enriched(val event: Event, val startMin: Int, val endMin: Int)

    val enriched = events
        //.filter { !it.isAllDay && it.startTime != null }
        .mapNotNull { ev ->
            val start = ev.startTime ?: return@mapNotNull null
            val dur = max(ev.duration ?: minDuration, minDuration)
            val startMin = start.toSecondOfDay() / 60
            val endMin = min(startMin + dur, minutesInDay)
            Enriched(ev, startMin, endMin)
        }
        .sortedWith(compareBy({ it.startMin }, { it.endMin }))

    if (enriched.isEmpty()) return emptyList()

    val groups = mutableListOf<MutableList<Enriched>>()
    var currentGroup = mutableListOf<Enriched>()
    var currentGroupMaxEnd = -1
    for (e in enriched) {
        if (currentGroup.isEmpty()) {
            currentGroup.add(e)
            currentGroupMaxEnd = e.endMin
        } else {
            if (e.startMin < currentGroupMaxEnd) {
                currentGroup.add(e)
                if (e.endMin > currentGroupMaxEnd) currentGroupMaxEnd = e.endMin
            } else {
                groups.add(currentGroup)
                currentGroup = mutableListOf(e)
                currentGroupMaxEnd = e.endMin
            }
        }
    }
    if (currentGroup.isNotEmpty()) groups.add(currentGroup)

    val result = mutableListOf<EventUIState>()
    for (group in groups) {
        val columnEndTimes = mutableListOf<Int>()
        val assignment = mutableMapOf<Enriched, Int>()

        val byStartThenEnd = group.sortedWith(compareBy({ it.startMin }, { it.endMin }))
        for (e in byStartThenEnd) {
            var placedIndex = -1
            for (i in columnEndTimes.indices) {
                if (e.startMin >= columnEndTimes[i]) {
                    placedIndex = i
                    columnEndTimes[i] = e.endMin
                    break
                }
            }
            if (placedIndex == -1) {
                columnEndTimes.add(e.endMin)
                placedIndex = columnEndTimes.lastIndex
            }
            assignment[e] = placedIndex
        }

        val columnsCount = max(columnEndTimes.size, 1)
        val baseWidth = 1f / columnsCount

        for ((i, e) in group.withIndex()) {
            val col = assignment[e] ?: 0
            val left = col * baseWidth
            val top = e.startMin
            val height = max(e.endMin - e.startMin, minDuration)
            val width = if (i == group.size - 1) baseWidth - 0.05f else baseWidth - 0.01f
            result.add(EventUIState(
                id = e.event.id,
                eventRef = e.event,
                startTime = e.event.startTime,
                endTime = e.event.startTime.plusMinutes(e.event.duration.toLong()),
                top = top,
                left = left,
                height = height,
                width = width,
            ))
        }
    }

    return result
}