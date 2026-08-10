package com.qyub.mgr2.presentation.screens.timeline.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
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
import com.qyub.mgr2.presentation.screens.timeline.TimelineUIState
import com.qyub.mgr2.presentation.screens.timeline.UIEvent
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("DefaultLocale")
@Composable
fun Timeline(
    modifier: Modifier = Modifier,
    uiState: TimelineUIState,
    day: LocalDate,
    onEventClick: (UIEvent) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    var currentMinuteOfDay by remember { mutableIntStateOf(getCurrentMinute()) }

    val totalMinutes = 24 * 60
    val density = LocalDensity.current
    val hourHeight = 60

    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

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
                        .width(40.dp),
                    contentAlignment = Alignment.TopStart
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

            uiState.eventItems.forEach { uiEvent ->
                TimelineCard(
                    event = uiEvent,
                    onClick = { onEventClick(it) },
                    fullWidth = maxWidth
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (day == LocalDate.now()) {
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