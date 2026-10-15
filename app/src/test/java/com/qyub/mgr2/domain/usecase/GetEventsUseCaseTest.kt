package com.qyub.mgr2.domain.usecase

import com.qyub.mgr2.domain.model.EventExceptionInfo
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class GetEventsUseCaseTest {

    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var useCase: GetEventsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeTaskRepository()
        useCase = GetEventsUseCase(fakeRepository)
    }

    @Test
    fun returnsBaseDailyTaskWithinRange() = runTest {
        val testDate = LocalDate.of(2026, 10, 15)
        val dailyTask = Task.Scheduled(
            id = 1,
            title = "Tägliches Meeting",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = LocalDate.of(2026, 10, 1),
            rangeEnd = LocalDate.of(2026, 10, 31),
            startTime = LocalTime.of(10, 0)
        )
        fakeRepository.setTasksForDate(testDate, listOf(dailyTask))

        val result = useCase(testDate).first()

        assertEquals(1, result.size)
        assertEquals("Tägliches Meeting", result[0].title)
    }

    @Test
    fun filtersOutBaseTaskOutsideRangeStart() = runTest {
        val testDate = LocalDate.of(2026, 10, 15)
        val futureTask = Task.Scheduled(
            id = 1,
            title = "Zukünftige Serie",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = LocalDate.of(2026, 10, 20),
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        fakeRepository.setTasksForDate(testDate, listOf(futureTask))

        val result = useCase(testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun filtersOutWeeklyTaskOnWrongDayOfWeek() = runTest {
        // 2026-10-15 ist ein Donnerstag (THURSDAY)
        val thursdayDate = LocalDate.of(2026, 10, 15)
        val mondayOnlyTask = Task.Scheduled(
            id = 1,
            title = "Montags-Meeting",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Weekly(setOf(DayOfWeek.MONDAY)),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        fakeRepository.setTasksForDate(thursdayDate, listOf(mondayOnlyTask))

        val result = useCase(thursdayDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun filtersOutCanceledExceptionForTheDay() = runTest {
        val testDate = LocalDate.of(2026, 10, 15)
        val baseTask = Task.Scheduled(
            id = 1,
            title = "Serien-Event",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        val canceledException = Task.Scheduled(
            id = 99,
            title = "Serien-Event",
            notes = null,
            color = 0xFF0000,
            duration = null,
            occurrence = Occurrence.FixedDate(testDate),
            rangeStart = testDate,
            rangeEnd = testDate,
            startTime = null,
            exceptionInfo = EventExceptionInfo(
                exceptionParentId = 1,
                originalDate = testDate,
                isCanceled = true
            )
        )
        fakeRepository.setTasksForDate(testDate, listOf(baseTask, canceledException))

        val result = useCase(testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun replacesBaseTaskWithNonCanceledExceptionOnOriginalDate() = runTest {
        val testDate = LocalDate.of(2026, 10, 15)
        val baseTask = Task.Scheduled(
            id = 1,
            title = "Original Titel",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        val modifiedException = Task.Scheduled(
            id = 2,
            title = "Geänderter Titel für heute",
            notes = "Sondernotiz",
            color = 0x00FF00,
            duration = Duration.ofMinutes(60),
            occurrence = Occurrence.FixedDate(testDate),
            rangeStart = testDate,
            rangeEnd = testDate,
            startTime = LocalTime.of(11, 0),
            exceptionInfo = EventExceptionInfo(
                exceptionParentId = 1,
                originalDate = testDate,
                isCanceled = false
            )
        )
        fakeRepository.setTasksForDate(testDate, listOf(baseTask, modifiedException))

        val result = useCase(testDate).first()

        assertEquals(1, result.size)
        assertEquals("Geänderter Titel für heute", result[0].title)
        assertEquals(LocalTime.of(11, 0), result[0].startTime)
    }
}

class FakeTaskRepository : TaskRepository {
    private val tasksMap = mutableMapOf<LocalDate, MutableList<Task>>()

    fun setTasksForDate(date: LocalDate, tasks: List<Task>) {
        tasksMap[date] = tasks.toMutableList()
    }

    override suspend fun insertTask(task: Task) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {}
    override suspend fun cancelOccurrence(parentTaskId: Int, originalDate: LocalDate) {}
    override suspend fun deleteTaskSeries(parentTaskId: Int) {}
    override suspend fun getTaskById(id: Int): Flow<Task> = flowOf()
    override suspend fun getEventsForDate(date: LocalDate): Flow<List<Task>> {
        return MutableStateFlow(tasksMap[date] ?: emptyList())
    }
    override suspend fun preloadEventsForDate(date: LocalDate) {}
}
