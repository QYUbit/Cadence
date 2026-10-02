package com.qyub.mgr2.presentation.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.presentation.screens.event.components.ColorPickerDialog
import com.qyub.mgr2.presentation.screens.event.components.DatePickerDialog
import com.qyub.mgr2.presentation.screens.event.components.MonthDatePickerDialog
import com.qyub.mgr2.presentation.screens.event.components.TimePickerDialog
import com.qyub.mgr2.presentation.screens.event.components.WeekdaySelection
import com.qyub.mgr2.presentation.screens.event.components.YearDatePickerDialog
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.MonthDay

// TODO Better defaults for UX

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditScreen(
    modifier: Modifier = Modifier,
    eventId: Int?,
    onBack: () -> Unit = {},
    viewModel: EventEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.loadEvent(eventId)
        } else {
            viewModel.resetState()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var occurrenceDropdownExpanded by remember { mutableStateOf(false) }
    var colorPickerOpen by remember { mutableStateOf(false) }

    val occurrenceTypes = listOf("Fixed Date", "Daily", "Weekly", "Monthly", "Yearly")
    val currentOccurrenceLabel = when (uiState.occurrence) {
        is Occurrence.FixedDate -> "Fixed Date"
        is Occurrence.Daily -> "Daily"
        is Occurrence.Weekly -> "Weekly"
        is Occurrence.Monthly -> "Monthly"
        is Occurrence.Yearly -> "Yearly"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (eventId != null) "Edit Event" else "New Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (eventId != null) {
                                    viewModel.submitEdit()
                                } else {
                                    viewModel.submitCreate()
                                }
                                onBack()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.notes ?: "",
                onValueChange = { viewModel.setNotes(it.ifEmpty { null }) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Occurrence Type Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = occurrenceDropdownExpanded,
                onExpandedChange = { occurrenceDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentOccurrenceLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Occurrence Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = occurrenceDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = occurrenceDropdownExpanded,
                    onDismissRequest = { occurrenceDropdownExpanded = false }
                ) {
                    occurrenceTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                occurrenceDropdownExpanded = false
                                val newOccurrence = when (type) {
                                    "Fixed Date" -> Occurrence.FixedDate(uiState.date)
                                    "Daily" -> Occurrence.Daily
                                    "Weekly" -> Occurrence.Weekly(setOf(DayOfWeek.MONDAY))
                                    "Monthly" -> Occurrence.Monthly(1)
                                    "Yearly" -> Occurrence.Yearly(MonthDay.of(1, 1))
                                    else -> Occurrence.FixedDate(uiState.date)
                                }
                                viewModel.setOccurrence(newOccurrence)
                            }
                        )
                    }
                }
            }

            // Dynamic occurrence fields
            when (val occ = uiState.occurrence) {
                is Occurrence.FixedDate -> {
                    OutlinedTextField(
                        value = occ.date.toString(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        enabled = false,
                        label = { Text("Date") },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                is Occurrence.Daily -> {
                    Text(
                        text = "This event repeats every day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is Occurrence.Weekly -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select days of the week:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        WeekdaySelection(
                            selected = occ.weekDays,
                            onSelectionChange = { days ->
                                viewModel.setOccurrence(Occurrence.Weekly(days))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                is Occurrence.Monthly -> {
                    OutlinedTextField(
                        value = "Day ${occ.monthDate} of every month",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMonthPicker = true },
                        enabled = false,
                        label = { Text("Monthly Day") },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick day of month")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                is Occurrence.Yearly -> {
                    OutlinedTextField(
                        value = "${occ.yearDate.month} ${occ.yearDate.dayOfMonth} every year",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showYearPicker = true },
                        enabled = false,
                        label = { Text("Yearly Date") },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date of year")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // All Day Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Day",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = uiState.isAllDay,
                    onCheckedChange = { viewModel.setAllDay(it) }
                )
            }

            // Start Time (only if not All Day)
            if (!uiState.isAllDay) {
                OutlinedTextField(
                    value = uiState.startTime?.toString() ?: "Not set",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartTimePicker = true },
                    enabled = false,
                    label = { Text("Start Time") },
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = "Pick start time")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Duration (minutes)
            OutlinedTextField(
                value = uiState.duration?.toMinutes()?.toString() ?: "30",
                onValueChange = { text ->
                    text.toLongOrNull()?.let { minutes ->
                        viewModel.setDurationMinutes(minutes)
                    }
                },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

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
                        .background(uiState.color)
                )
                Text("Event Appearance")
            }
        }

        if (colorPickerOpen) {
            ColorPickerDialog(
                selected = uiState.color,
                onSelect = { selected ->
                    viewModel.setColor(selected)
                    colorPickerOpen = false
                },
                onDismissRequest = { colorPickerOpen = false }
            )
        }

        if (showDatePicker) {
            val currentDate = (uiState.occurrence as? Occurrence.FixedDate)?.date ?: uiState.date
            DatePickerDialog(
                initialDate = currentDate,
                onDismiss = { showDatePicker = false },
                onConfirm = { date ->
                    viewModel.setOccurrence(Occurrence.FixedDate(date))
                    showDatePicker = false
                }
            )
        }

        if (showMonthPicker) {
            val currentDay = (uiState.occurrence as? Occurrence.Monthly)?.monthDate ?: 1
            MonthDatePickerDialog(
                initialDay = currentDay,
                onDismiss = { showMonthPicker = false },
                onConfirm = { day ->
                    viewModel.setOccurrence(Occurrence.Monthly(day))
                    showMonthPicker = false
                }
            )
        }

        if (showYearPicker) {
            val yearDate = (uiState.occurrence as? Occurrence.Yearly)?.yearDate ?: MonthDay.of(1, 1)
            YearDatePickerDialog(
                initialDay = yearDate.dayOfMonth,
                initialMonth = yearDate.monthValue,
                onDismiss = { showYearPicker = false },
                onConfirm = { month, day ->
                    viewModel.setOccurrence(Occurrence.Yearly(MonthDay.of(month, day)))
                    showYearPicker = false
                }
            )
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                initialTime = uiState.startTime ?: LocalTime.now(),
                onDismiss = { showStartTimePicker = false },
                onConfirm = {
                    viewModel.setStartTime(it)
                    showStartTimePicker = false
                }
            )
        }
    }
}
