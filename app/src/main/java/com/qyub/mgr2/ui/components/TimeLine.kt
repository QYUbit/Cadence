package com.qyub.mgr2.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.ui.theme.EventColorCrimson
import com.qyub.mgr2.ui.theme.EventColorLavender
import com.qyub.mgr2.ui.theme.EventColorMint
import com.qyub.mgr2.ui.theme.EventColorSky
import com.qyub.mgr2.ui.theme.EventColorSun
import java.time.LocalTime
import kotlin.math.max
import kotlin.math.min

data class UIEvent(
    val event: Event,
    val top: Int,
    val height: Int,
    val left: Float,
    val width: Float
)

private val sampleEvents = listOf(
    Event(
        id = 0,
        title = "Example",
        description = "go shopping",
        startTime = LocalTime.of(16, 30),
        duration = 30,
        color = EventColorMint
    ),
    Event(
        id = 1,
        title = "Team Meeting",
        description = "Weekly sync with the dev team",
        startTime = LocalTime.of(10, 0),
        duration = 120,
        color = EventColorSky
    ),
    Event(
        id = 2,
        title = "Lunch Break",
        description = "Quick meal at the café",
        startTime = LocalTime.of(12, 30),
        duration = 60,
        color = EventColorSun
    ),
    Event(
        id = 3,
        title = "Gym Session",
        description = "Cardio and weights",
        startTime = LocalTime.of(18, 0),
        duration = 10,
        color = EventColorLavender
    ),
    Event(
        id = 4,
        title = "Dinner with Friends",
        description = "Italian restaurant reservation",
        startTime = LocalTime.of(20, 0),
        duration = 120,
        color = EventColorCrimson
    )
)

@SuppressLint("DefaultLocale")
@Composable
fun Timeline(
    events: List<Event>,
    isCurrentDay: Boolean,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit
) {
    val totalMinutes = 24 * 60
    val density = LocalDensity.current
    val hourHeight = 60

    val uiEvents = getEventDimensions(events)

    var currentMinuteOfDay by remember { mutableIntStateOf(getCurrentMinute()) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentMinuteOfDay = getCurrentMinute()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

    Row(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .height((24 * hourHeight).dp)
        ) {
            for (hour in 0..23) {
                Box(
                    modifier = Modifier
                        .height(hourHeight.dp)
                        .width(56.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = String.format("%02d:00", hour),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        BoxWithConstraints (
            modifier = Modifier
                .weight(1f)
                .height(totalMinutes.dp)
                .padding(top = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (hour in 0..23) {
                    val y = with(density) { (hour * 60).dp.toPx() }
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y)
                    )
                }
            }

            uiEvents.forEach { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.event) },
                    fullWidth = maxWidth
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (isCurrentDay) {
                    val y = with(density) { currentMinuteOfDay.toFloat().dp.toPx() }
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 8f
                    )
                    drawCircle(
                        color = Color.Red,
                        radius = 16f,
                        center = Offset(0f, y)
                    )
                }
            }
        }
    }
}

fun getCurrentMinute(): Int {
    val now = LocalTime.now()
    return now.hour * 60 + now.minute
}

fun getAllDayEvents(events: List<Event>): List<UIEvent> {
    return events
        .filter { it.isAllDay }
        .map { UIEvent(it, 0, 30, 0f, 1f) }
}

fun getEventDimensions(events: List<Event>): List<UIEvent> {
    val minDuration = 10
    val minutesInDay = 24 * 60

    data class Enriched(val event: Event, val startMin: Int, val endMin: Int)

    val enriched = events
        .filter { !it.isAllDay && it.startTime != null }
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

    val result = mutableListOf<UIEvent>()
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
            result.add(UIEvent(e.event, top, height, left, width))
        }
    }

    return result
}
