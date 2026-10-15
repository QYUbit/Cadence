package com.qyub.mgr2.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qyub.mgr2.data.db.AppDatabase
import com.qyub.mgr2.data.db.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetEventById() = runBlocking {
        val task = TaskEntity(
            id = 10,
            title = "Test Task",
            notes = "Beschreibung",
            color = 0xFF0000,
            duration = 30
        )
        taskDao.insert(task)

        val retrieved = taskDao.getEventById(10).first()

        assertEquals("Test Task", retrieved.title)
        assertEquals("Beschreibung", retrieved.notes)
        assertEquals(30L, retrieved.duration)
    }

    @Test
    fun getEventsForDateReturnsFixedDateAndSeriesCandidates() = runBlocking {
        val todayEpoch = LocalDate.of(2026, 10, 15).toEpochDay()
        val tomorrowEpoch = LocalDate.of(2026, 10, 16).toEpochDay()

        // 1. Fester Termin für heute
        val todayTask = TaskEntity(
            id = 1,
            title = "Heute Fest",
            notes = null,
            color = 0xFF0000,
            occurrenceType = "FixedDate",
            fixedDate = todayEpoch,
            duration = null
        )

        // 2. Fester Termin für morgen
        val tomorrowTask = TaskEntity(
            id = 2,
            title = "Morgen Fest",
            notes = null,
            color = 0xFF0000,
            occurrenceType = "FixedDate",
            fixedDate = tomorrowEpoch,
            duration = null
        )

        // 3. Täglich wiederkehrende Serie (kein Exception-Kind)
        val dailySeriesTask = TaskEntity(
            id = 3,
            title = "Tägliche Serie",
            notes = null,
            color = 0x00FF00,
            occurrenceType = "Daily",
            duration = null
        )

        taskDao.insert(todayTask)
        taskDao.insert(tomorrowTask)
        taskDao.insert(dailySeriesTask)

        val eventsForToday = taskDao.getEventsForDate(todayEpoch).first()

        assertEquals(2, eventsForToday.size)
        val titles = eventsForToday.map { it.title }
        assertTrue(titles.contains("Heute Fest"))
        assertTrue(titles.contains("Tägliche Serie"))
    }

    @Test
    fun deleteSeriesRemovesParentAndAllExceptions() = runBlocking {
        val dateA = LocalDate.of(2026, 10, 15).toEpochDay()
        val dateB = LocalDate.of(2026, 10, 16).toEpochDay()

        // Parent Serie
        val parentTask = TaskEntity(
            id = 100,
            title = "Hauptserie",
            notes = null,
            color = 0xFF0000,
            occurrenceType = "Daily",
            duration = null
        )

        // Exception 1
        val exception1 = TaskEntity(
            id = 101,
            exceptionParentId = 100,
            originalDate = dateA,
            title = "Ausnahme 1",
            notes = null,
            color = 0xFF0000,
            occurrenceType = "FixedDate",
            fixedDate = dateA,
            duration = null
        )

        // Exception 2
        val exception2 = TaskEntity(
            id = 102,
            exceptionParentId = 100,
            originalDate = dateB,
            title = "Ausnahme 2",
            notes = null,
            color = 0xFF0000,
            occurrenceType = "FixedDate",
            fixedDate = dateB,
            duration = null
        )

        // Unbeteiligte Aufgabe
        val otherTask = TaskEntity(
            id = 200,
            title = "Andere Aufgabe",
            notes = null,
            color = 0x0000FF,
            occurrenceType = "Daily",
            duration = null
        )

        taskDao.insert(parentTask)
        taskDao.insert(exception1)
        taskDao.insert(exception2)
        taskDao.insert(otherTask)

        // Serie löschen
        taskDao.deleteSeries(100)

        val parentInDb = taskDao.getEventById(100).first()
        val exc1InDb = taskDao.getEventById(101).first()
        val exc2InDb = taskDao.getEventById(102).first()
        val otherInDb = taskDao.getEventById(200).first()

        assertNull(parentInDb)
        assertNull(exc1InDb)
        assertNull(exc2InDb)
        assertEquals("Andere Aufgabe", otherInDb.title)
    }
}
