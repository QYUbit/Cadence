package com.qyub.mgr2.presentation.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qyub.mgr2.presentation.screens.event.components.DatePickerDialog
import com.qyub.mgr2.presentation.screens.event.components.TimePickerDialog
import kotlinx.coroutines.launch

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
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 24.dp)
            .navigationBarsPadding()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                if (eventId != null) "Edit ${uiState.title}" else "Create Event",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(
                onClick = {
                    scope.launch {
                        if (eventId != null) viewModel.submitEdit() else viewModel.submitCreate()
                        onBack()
                    }
                },
                modifier = Modifier.background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        TextField(
            value = uiState.title,
            onValueChange = { viewModel.setTitle(it) }
        )

        OutlinedTextField(
            value = uiState.date.toString(),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            label = { Text("Date") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                initialDate = uiState.date,
                onDismiss = { showDatePicker = false },
                onConfirm = {
                    viewModel.setDate(it)
                    showDatePicker = false
                }
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            OutlinedTextField(
                value = uiState.startTime.toString(),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .clickable { showStartTimePicker = true },
                enabled = false,
                label = { Text("Start time") }
            )

            OutlinedTextField(
                value = uiState.endTime.toString(),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clickable { showEndTimePicker = true },
                enabled = false,
                label = { Text("End time") }
            )
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                initialTime = uiState.startTime,
                onDismiss = { showStartTimePicker = false },
                onConfirm = {
                    viewModel.setStartTime(it)
                    showStartTimePicker = false
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                initialTime = uiState.endTime,
                onDismiss = { showEndTimePicker = false },
                onConfirm = {
                    viewModel.setEndTime(it)
                    showEndTimePicker = false
                }
            )
        }
    }
}