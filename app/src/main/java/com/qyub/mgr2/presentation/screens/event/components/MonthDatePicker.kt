package com.qyub.mgr2.presentation.screens.event.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDatePickerDialog(
    initialDay: Int = 1,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(initialDay) }

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDay) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose date",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grid für die Tage 1-31
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp)
            ) {
                items(31) { index ->
                    val day = index + 1
                    DayElement(
                        day = day,
                        isSelected = selectedDay == day,
                        onClick = { selectedDay = day }
                    )
                }
            }
        }
    }
}