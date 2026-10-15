package com.qyub.mgr2.presentation.screens.timeline

import androidx.compose.ui.graphics.Color
import com.qyub.mgr2.domain.model.EventExceptionInfo
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.domain.model.Task
import com.qyub.mgr2.domain.repository.TaskRepository
import com.qyub.mgr2.domain.usecase.GetEventsUseCase
import com.qyub.mgr2.domain.usecase.PreloadEventsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepository: FakeViewModelTaskRepository
    private lateinit var viewModel: TimelineViewModel

    private val today = LocalDate.of(2026, 10, 15)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeViewModelTaskRepository()
        val getEventsUseCase = GetEventsUseCase(fakeRepository)
        val preloadEventsUseCase = PreloadEventsUseCase(fakeRepository)

        viewModel = TimelineViewModel(
            getEventsUseCase = getEventsUseCase,
            preloadEventsUseCase = preloadEventsUseCase,
            eventRepository = fakeRepository
        )
        viewModel.setDay(today)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setDayUpdatesDisplayDayInUiState() = runTest {
        val job = backgroundScope.launch { viewModel.uiState.collect {} }
        val newDay = LocalDate.of(2026, 12, 24)
        viewModel.setDay(newDay)

        val state = viewModel.uiState.first { it.displayDay == newDay }
        assertEquals(newDay, state.displayDay)
        job.cancel()
    }

    @Test
    fun setInspectedEventUpdatesInspectedEventInUiState() = runTest {
        val job = backgroundScope.launch { viewModel.uiState.collect {} }
        val task = Task.Scheduled(
            id = 1,
            title = "Inspektionstermin",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        val eventUIState = EventUIState(
            id = 1,
            taskRef = task,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30),
            top = 600,
            left = 0f,
            height = 30,
            width = 0.95f,
            color = Color.Red
        )

        viewModel.setInspectedEvent(eventUIState)

        val state = viewModel.uiState.first { it.inspectedEvent == eventUIState }
        assertEquals(eventUIState, state.inspectedEvent)
        job.cancel()
    }

    @Test
    fun onDeleteOccurrenceForSeriesTriggersCancelOccurrence() = runTest {
        val seriesTask = Task.Scheduled(
            id = 10,
            title = "Serie",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        val eventUI = EventUIState(
            id = 10,
            taskRef = seriesTask,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30),
            top = 600,
            left = 0f,
            height = 30,
            width = 0.95f,
            color = Color.Red
        )

        viewModel.onDeleteOccurrence(eventUI)

        assertEquals(1, fakeRepository.canceledOccurrences.size)
        assertEquals(10 to today, fakeRepository.canceledOccurrences[0])
    }

    @Test
    fun onDeleteOccurrenceForFixedDateTriggersDeleteTask() = runTest {
        val fixedTask = Task.Scheduled(
            id = 20,
            title = "Fester Termin",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.FixedDate(today),
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0)
        )
        val eventUI = EventUIState(
            id = 20,
            taskRef = fixedTask,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30),
            top = 600,
            left = 0f,
            height = 30,
            width = 0.95f,
            color = Color.Red
        )

        viewModel.onDeleteOccurrence(eventUI)

        assertEquals(1, fakeRepository.deletedTasks.size)
        assertEquals(20, fakeRepository.deletedTasks[0].id)
    }

    @Test
    fun onDeleteSeriesTriggersDeleteTaskSeries() = runTest {
        val seriesTask = Task.Scheduled(
            id = 10,
            title = "Serie",
            notes = null,
            color = 0xFF0000,
            duration = Duration.ofMinutes(30),
            occurrence = Occurrence.Daily,
            rangeStart = null,
            rangeEnd = null,
            startTime = LocalTime.of(10, 0),
            exceptionInfo = EventExceptionInfo(
                exceptionParentId = 100,
                originalDate = today,
                isCanceled = false
            )
        )
        val eventUI = EventUIState(
            id = 10,
            taskRef = seriesTask,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30),
            top = 600,
            left = 0f,
            height = 30,
            width = 0.95f,
            color = Color.Red
        )

        viewModel.onDeleteSeries(eventUI)

        assertEquals(1, fakeRepository.deletedSeriesIds.size)
        assertEquals(100, fakeRepository.deletedSeriesIds[0])
    }
}

class FakeViewModelTaskRepository : TaskRepository {
    val deletedTasks = mutableListOf<Task>()
    val canceledOccurrences = mutableListOf<Pair<Int, LocalDate>>()
    val deletedSeriesIds = mutableListOf<Int>()

    override suspend fun insertTask(task: Task) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {
        deletedTasks.add(task)
    }
    override suspend fun cancelOccurrence(parentTaskId: Int, originalDate: LocalDate) {
        canceledOccurrences.add(parentTaskId to originalDate)
    }
    override suspend fun deleteTaskSeries(parentTaskId: Int) {
        deletedSeriesIds.add(parentTaskId)
    }
    override suspend fun getTaskById(id: Int): Flow<Task> = flowOf()
    override suspend fun getEventsForDate(date: LocalDate): Flow<List<Task>> = MutableStateFlow(emptyList())
    override suspend fun preloadEventsForDate(date: LocalDate) {}
}
