package com.qyub.mgr2.presentation.screens.event.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek

@Composable
fun WeekdaySelection(
    selected: Set<DayOfWeek>,
    onSelectionChange: (Set<DayOfWeek>) -> Unit,
    modifier: Modifier = Modifier,
    chipSize: Dp = 48.dp,
    spacing: Dp = 12.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.surface,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    singleSelection: Boolean = false
) {
    val allWeekDays = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        allWeekDays.forEach { weekDay ->
            val label = weekDay.name[0].toString()
            val isSelected = selected.contains(weekDay)

            val backgroundColor by animateColorAsState(targetValue = if (isSelected) selectedColor else unselectedColor,
                label = "bgColor"
            )
            val textColor by animateColorAsState(targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                label = "txtColor"
            )

            Box(
                modifier = Modifier
                    .size(chipSize)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable {
                        val newSet = if (singleSelection) {
                            if (isSelected) emptySet() else setOf(weekDay)
                        } else {
                            selected.toMutableSet().apply {
                                if (isSelected) remove(weekDay) else add(weekDay)
                            }
                        }
                        onSelectionChange(newSet)
                    }
                    .semantics { contentDescription = "Week day ${label}, ${if (isSelected) "selected" else "not selected"}" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}