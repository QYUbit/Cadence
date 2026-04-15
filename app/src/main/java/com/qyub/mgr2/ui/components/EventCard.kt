package com.qyub.mgr2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qyub.mgr2.data.models.Event

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    event: Event,
    onClick: (Event) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = event.color ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .padding(6.dp)
            .clickable { onClick(event) },
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
        )
    }
}