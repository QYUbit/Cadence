package com.qyub.mgr2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningDialog(
    eventName: String,
    onConfirm: (date: LocalDate, time: LocalTime?) -> Unit,
    onDismissRequest: () -> Unit
) {
    var date by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var duration by remember { mutableIntStateOf(30) }

    var isAllDay by remember { mutableStateOf(false) }

    var startTimePickerOpen by remember { mutableStateOf(false) }
    var endTimePickerOpen by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }

    FullScreenDialog(
        title = eventName,
        onDismiss = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
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

            if (!isAllDay) {
                Row(
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

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                TextButton(
                    onClick = {
                    }
                ) {
                    Text("Confirm")
                }
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