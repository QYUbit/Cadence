package com.qyub.mgr2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.data.models.NotificationType
import com.qyub.mgr2.data.models.RepeatType
import com.qyub.mgr2.ui.theme.PrimaryLight
import java.time.LocalDate
import java.time.LocalTime
import java.time.MonthDay

val repeatLabels = listOf(
    RepeatType.NONE to "Once",
    RepeatType.DAILY to "Daily",
    RepeatType.WEEK_DAY to "For specific week days",
    RepeatType.MONTH_DAY to "Once a month",
    RepeatType.YEAR_DAY to "Once a year",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBottomSheet(
    initialEvent: Event? = null,
    currentDate: LocalDate = LocalDate.now(),
    isInbox: Boolean = false,
    onSave: (Event) -> Unit,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }
    var isInboxEvent by remember { mutableStateOf(initialEvent?.isInbox ?: isInbox) }
    var isRepeating by remember { mutableStateOf(initialEvent?.isRepeating ?: false) }
    var repeatType by remember { mutableStateOf(initialEvent?.repeatFor ?: RepeatType.NONE) }
    var repeatAt by remember { mutableStateOf(initialEvent?.repeatAt) }
    var date by remember { mutableStateOf(initialEvent?.date ?: if (!isInbox) currentDate else null) }
    var isAllDay by remember { mutableStateOf(initialEvent?.isAllDay ?: false) }
    var startTime by remember { mutableStateOf(initialEvent?.startTime ?: LocalTime.of(LocalTime.now().hour + 1 , 0)) }
    var duration by remember { mutableIntStateOf(initialEvent?.duration ?: 60) }
    var color by remember { mutableStateOf(initialEvent?.color ?: PrimaryLight) }
    var hasReminder by remember { mutableStateOf(initialEvent?.hasNotification ?: false) }
    val reminderType by remember { mutableStateOf(initialEvent?.notificationType ?: NotificationType.REMINDER) }
    var reminderMinutes by remember { mutableIntStateOf(initialEvent?.notificationMinutes ?: 15) }

    var startTimePickerOpen by remember { mutableStateOf(false) }
    var endTimePickerOpen by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }
    var colorPickerOpen by remember { mutableStateOf(false) }
    var notificationTimePickerOpen by remember { mutableStateOf(false) }
    var repeatPickerOpen by remember { mutableStateOf(false) }
    var monthDatePickerOpen by remember { mutableStateOf(false) }
    var yearDatePickerOpen by remember { mutableStateOf(false) }

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = modalBottomSheetState,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 500.dp, max = 700.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val canDelete = initialEvent != null && onDelete != null

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            if (canDelete) {
                                onDelete()
                            }
                            onDismiss()
                        }
                ) {
                    Icon(
                        if (canDelete) Icons.Default.Delete else Icons.Default.Close,
                        contentDescription = if (canDelete) "Delete Event" else "Discard Changes",
                        tint = if (canDelete) Color.Red else MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    if (initialEvent == null) "Create new event" else "Edit Event",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(if (initialEvent == null) "Create" else "Save",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onSave(
                            Event(
                                id = initialEvent?.id ?: 0,
                                title = title,
                                isInbox = isInboxEvent,
                                description = description.ifEmpty { null },
                                isRepeating = isRepeating,
                                repeatFor = repeatType,
                                repeatAt = repeatAt,
                                date = if (!isRepeating) date else null,
                                isAllDay = isAllDay,
                                startTime = if (isAllDay) null else startTime,
                                duration = if (isAllDay) null else duration,
                                color = color,
                                hasNotification = if (isAllDay) false else hasReminder,
                                notificationType = reminderType,
                                notificationMinutes = reminderMinutes,
                            )
                        )
                        onDismiss()
                    }
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isInbox) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { repeatPickerOpen = true }
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Repeat: ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = repeatLabels.filter { it.first == repeatType }
                                .map { it.second }[0],
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (repeatPickerOpen) {
                ModalPicker(
                    options = repeatLabels,
                    currentValue = repeatType,
                    onDismissRequest = { repeatPickerOpen = false },
                    onSelect = { type ->
                        isRepeating = type != RepeatType.NONE
                        repeatType = type
                        repeatAt = emptyList()
                        repeatPickerOpen = false
                    },
                    title = "Select repeating rule"
                )
            }

            if (repeatType == RepeatType.NONE && !isInbox) {
                OutlinedTextField(
                    value = date.toString(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerOpen = true },
                    enabled = false,
                    label = { Text("Date") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                )
            }

            if (repeatType == RepeatType.WEEK_DAY) {
                Text("Repeat on", color = MaterialTheme.colorScheme.onSurfaceVariant)

                WeekdaySelection(
                    selected = repeatAt?.toSet() ?: emptySet(),
                    onSelectionChange = { newDays -> repeatAt = newDays.toList() },
                    chipSize = 32.dp,
                )
            }

            if (repeatType == RepeatType.MONTH_DAY) {
                OutlinedTextField(
                    value = repeatAt?.joinToString(", ") { formatNumber(it) } ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { monthDatePickerOpen = true },
                    enabled = false,
                    label = { Text("Day of month") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                )
            }

            if (repeatType == RepeatType.YEAR_DAY) {
                OutlinedTextField(
                    value = if (repeatAt != null && repeatAt!!.size == 2) "${repeatAt!![0]}.${repeatAt!![1]}." else "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { yearDatePickerOpen = true },
                    enabled = false,
                    label = { Text("Day of year") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                )
            }

            if (datePickerOpen) {
                DatePickerDialog(
                    initialDate = date ?: LocalDate.now(),
                    onDismiss = { datePickerOpen = false },
                    onConfirm = {
                        date = it
                        datePickerOpen = false
                    }
                )
            }

            if (monthDatePickerOpen) {
                MonthDateDialog(
                    selectedDays = repeatAt ?: emptyList(),
                    onSelect = { day ->
                        repeatAt = emptyList()

                        if (repeatAt!!.contains(day)) {
                            repeatAt = repeatAt!!.filter { it != day }
                        } else {
                            repeatAt!!.plus(day)
                        }

                    },
                    onDismissRequest = { monthDatePickerOpen = false }
                )
            }

            if (yearDatePickerOpen) {
                YearDateDialog(
                    selectedDate = if (repeatAt != null && repeatAt!!.size == 2)
                        MonthDay.of(repeatAt!![0], repeatAt!![1]) else MonthDay.now(),
                    onSelect = { day ->
                        repeatAt = listOf(day.monthValue, day.dayOfMonth)
                    },
                    onDismissRequest = { yearDatePickerOpen = false }
                )
            }

            if (!isInbox) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Is all-day?")
                    Switch(checked = isAllDay, onCheckedChange = { isAllDay = it })
                }
            }

            if (!isAllDay && !isInbox) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = startTime.toString(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .clickable { startTimePickerOpen = true },
                        enabled = false,
                        label = { Text("Start time") }
                    )

                    OutlinedTextField(
                        value = getEndTime(startTime, duration).toString(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clickable { endTimePickerOpen = true },
                        enabled = false,
                        label = { Text("End time") }
                    )
                }
            }

            if (startTimePickerOpen) {
                TimePickerDialog(
                    initialTime = startTime,
                    onDismiss = { startTimePickerOpen = false },
                    onConfirm = { selected ->
                        startTime = selected
                        startTimePickerOpen = false
                    }
                )
            }

            if (endTimePickerOpen) {
                TimePickerDialog(
                    initialTime = getEndTime(startTime, duration),
                    onDismiss = { endTimePickerOpen = false },
                    onConfirm = { selected ->
                        duration = getDuration(startTime, selected)
                        endTimePickerOpen = false
                    }
                )
            }

            Row (
                modifier = Modifier
                    .clickable { colorPickerOpen = true },
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text("Event Appearance")
            }

            if (colorPickerOpen) {
                ColorPickerDialog(
                    selected = color,
                    onSelect = { selected ->
                        color = selected
                        colorPickerOpen = false
                    },
                    onDismissRequest = { colorPickerOpen = false }
                )
            }

            Column {
                if (!isAllDay && !isInbox) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Reminder",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Switch(
                            checked = hasReminder,
                            onCheckedChange = { hasReminder = it }
                        )
                    }
                }

                if (hasReminder && !isAllDay && !isInbox) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { notificationTimePickerOpen = true }
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Reminder",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formatNotificationTime(reminderMinutes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (notificationTimePickerOpen) {
                ModalPicker(
                    options = listOf(
                        0 to "At start time",
                        5 to "5 minutes before",
                        10 to "10 minutes before",
                        15 to "15 minutes before",
                        30 to "30 minutes before",
                        60 to "1 hour before",
                        120 to "2 hours before",
                        1440 to "1 day before"
                    ),
                    currentValue = reminderMinutes,
                    onDismissRequest = { notificationTimePickerOpen = false },
                    onSelect = { minutes ->
                        reminderMinutes = minutes
                        notificationTimePickerOpen = false
                    },
                    title = "Select reminder"
                )
            }
        }
    }
}

private fun getDuration(start: LocalTime, end: LocalTime): Int {
    val startMinutes = start.toSecondOfDay() / 60
    val endMinutes = end.toSecondOfDay() / 60
    return endMinutes - startMinutes
}

private fun getEndTime(start: LocalTime, duration: Int): LocalTime {
    return start.plusMinutes(duration.toLong())
}

private fun formatNotificationTime(minutes: Int): String {
    return when (minutes) {
        0 -> "At start time"
        in 1..59 -> "$minutes min before"
        60 -> "1 hour before"
        in 61..1439 -> "${minutes / 60} hours before"
        1440 -> "1 day before"
        else -> "${minutes / 1440} days before"
    }
}

private fun formatNumber(number: Int): String {
    return when (number) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${number}th"
    }
}
