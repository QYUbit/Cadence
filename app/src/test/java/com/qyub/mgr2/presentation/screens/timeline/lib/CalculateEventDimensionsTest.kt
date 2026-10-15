package com.qyub.mgr2.presentation.screens.timeline.lib

import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class CalculateEventDimensionsTest {

    private val today = LocalDate.of(2026, 10, 15)

    @Test
    fun returnsEmptyListWhenInputIsEmpty() {
        val result = calculateEventDimensions(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun returnsEmptyListWhenEventsHaveNoStartTime() {
        val unscheduledLikeTask = Task.Scheduled(
            id = 1,
            title = "Ganztägig",
            notes = null,
            color = 0xFF0000,
            duration = null,
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = null
        )

        val result = calculateEventDimensions(listOf(unscheduledLikeTask))
        assertTrue(result.isEmpty())
    }

    @Test
    fun calculatesSingleEventDimensionsCorrectly() {
        val singleTask = Task.Scheduled(
            id = 1,
            title = "Meeting",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(60),
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(9, 0) // 540 Minuten
        )

        val result = calculateEventDimensions(listOf(singleTask))

        assertEquals(1, result.size)
        val eventUI = result[0]
        assertEquals(1, eventUI.id)
        assertEquals(540, eventUI.top)
        assertEquals(60, eventUI.height)
        assertEquals(0f, eventUI.left, 0.001f)
        assertEquals(0.95f, eventUI.width, 0.001f)
    }

    @Test
    fun calculatesMultipleOverlappingEventsInSeparateColumns() {
        val eventA = Task.Scheduled(
            id = 1,
            title = "Event A",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(60),
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(9, 0) // 09:00 - 10:00
        )
        val eventB = Task.Scheduled(
            id = 2,
            title = "Event B",
            notes = null,
            color = 0x00FF00,
            duration = Duration.ofMinutes(60),
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(9, 15) // 09:15 - 10:15 (Überschneidung!)
        )

        val result = calculateEventDimensions(listOf(eventA, eventB))

        assertEquals(2, result.size)

        val uiA = result.first { it.id == 1 }
        val uiB = result.first { it.id == 2 }

        assertEquals(0f, uiA.left, 0.001f)
        assertEquals(0.5f, uiB.left, 0.001f)
        assertEquals(0.49f, uiA.width, 0.001f)
        assertEquals(0.45f, uiB.width, 0.001f)
    }

    @Test
    fun enforcesMinimumDurationOfTenMinutes() {
        val shortTask = Task.Scheduled(
            id = 1,
            title = "Kurzer Standup",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(2), // kleiner als 10 Min
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )

        val result = calculateEventDimensions(listOf(shortTask))

        assertEquals(1, result.size)
        assertEquals(10, result[0].height)
    }
}
