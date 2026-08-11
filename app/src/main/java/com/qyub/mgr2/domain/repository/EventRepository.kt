package com.qyub.mgr2.domain.repository

import com.qyub.mgr2.domain.model.Event
import java.time.LocalDate

interface EventRepository {
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun getEventById(id: Int): Event
    suspend fun getEventsForDate(date: LocalDate): List<Event>
    suspend fun preloadEventsForDate(date: LocalDate)
}