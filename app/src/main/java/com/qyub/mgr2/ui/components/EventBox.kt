package com.qyub.mgr2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qyub.mgr2.data.models.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EventBox(
    event: UIEvent,
    onClick: (event: Event) -> Unit,
    fullWidth: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = fullWidth.times(event.left), y = event.top.dp)
            .fillMaxWidth(event.width)
            .height(event.height.dp)
            .background(
                color = event.event.color ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick(event.event) },
        contentAlignment = Alignment.TopStart
    ) {
        Column (
            modifier = Modifier.padding(if (event.height <= 40) 6.dp else 12.dp)
        ) {
            if (event.height >= 30) {
                Text(
                    text = event.event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                )
            }

            if (event.height >= 60) {
                Text(
                    text = if (event.event.startTime != null && event.event.duration != null)
                        "${event.event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${getEventEnd(event).format(DateTimeFormatter.ofPattern("HH:mm"))}"
                    else "",
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
    return event.event.startTime?.plusMinutes((event.event.duration ?: 0).toLong()) ?: LocalTime.of(0, 0)
}