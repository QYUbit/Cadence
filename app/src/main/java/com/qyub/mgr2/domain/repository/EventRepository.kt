package com.qyub.mgr2.domain.repository

import com.qyub.mgr2.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EventRepository {
    suspend fun insertEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun getEventById(id: Int): Flow<Event>
    suspend fun getEventsForDate(date: LocalDate): Flow<List<Event>>
    suspend fun preloadEventsForDate(date: LocalDate)
}