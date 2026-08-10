package com.qyub.mgr2.presentation.screens.timeline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import com.qyub.mgr2.presentation.screens.timeline.UIEvent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimelineCard(
    event: UIEvent,
    onClick: (UIEvent) -> Unit,
    fullWidth: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = fullWidth.times(event.left), y = event.top.dp)
            .fillMaxWidth(event.width)
            .height(event.height.dp)
            .background(
                color = event.color,
                shape = MaterialTheme.shapes.small
            )
            .pointerInput(event.id) {
                detectTapGestures(
                    onTap = { onClick(event) },
                )
            },
        contentAlignment = Alignment.TopStart
    ) {
        Column (
            modifier = Modifier.padding(if (event.height <= 40) 6.dp else 12.dp)
        ) {
            if (event.height >= 30) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                )
            }

            if (event.height >= 60) {
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${getEventEnd(event).format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

private fun getEventEnd(event: UIEvent): LocalTime {
    return event.startTime.plusMinutes((event.duration).toLong()) ?: LocalTime.of(0, 0)
}