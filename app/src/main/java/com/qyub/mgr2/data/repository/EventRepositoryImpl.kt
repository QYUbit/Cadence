package com.qyub.mgr2.data.repository

import com.qyub.mgr2.data.db.dao.EventDao
import com.qyub.mgr2.data.db.entity.toEntity
import com.qyub.mgr2.data.db.entity.toEvent
import com.qyub.mgr2.domain.model.Event
import com.qyub.mgr2.domain.repository.EventRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {
    val eventCache = mutableMapOf<LocalDate, List<Event>>()

    override suspend fun insertEvent(event: Event) {
        eventDao.insert(event.toEntity())
    }

    override suspend fun getEventsForDate(date: LocalDate): List<Event> {
        return eventCache.getOrPut(date) {
            eventDao.getEventsForDate(date.toEpochDay()).map { it.toEvent() }
        }
    }

    override suspend fun preloadEventsForDate(date: LocalDate) {
        eventCache.putIfAbsent(date, eventDao.getEventsForDate(date.toEpochDay()).map { it.toEvent() })
    }
}