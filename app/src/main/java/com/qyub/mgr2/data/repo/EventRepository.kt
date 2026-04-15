package com.qyub.mgr2.data.repo

import android.content.Context
import com.qyub.mgr2.data.db.EventDao
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.data.models.isActiveAtDate
import com.qyub.mgr2.data.notifications.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val dao: EventDao,
    @ApplicationContext private val context: Context
) {
    // TODO Hilt DI
    private val notificationScheduler by lazy {
        NotificationScheduler(context)
    }

    fun allEvents(): Flow<List<Event>> {
        return dao.allEvents()
    }

    fun eventsForDate(date: LocalDate): Flow<List<Event>> {
        return dao
            .getEventsForDate(date.toEpochDay())
            .map { events ->
                events.filter { it.isActiveAtDate(date) }
            }
    }

    fun inboxEvents(): Flow<List<Event>> {
        return dao.getInboxEvents()
    }

    suspend fun addEvent(event: Event) {
        dao.insert(event)

        if (event.hasNotification) {
            notificationScheduler.scheduleNotification(event, event.notificationMinutes)
        }
    }

    suspend fun updateEvent(event: Event) {
        dao.update(event)

        if (event.hasNotification) {
            notificationScheduler.rescheduleNotification(event, event.notificationMinutes)
        } else {
            notificationScheduler.cancelNotification(event.id, event.notificationMinutes)
        }
    }

    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        dao.delete(event)

        notificationScheduler.cancelNotification(event.id, event.notificationMinutes)
    }

    suspend fun deleteAllEvents() = withContext(Dispatchers.IO) {
        dao.deleteAll()
        // cancel notifications
    }
}
