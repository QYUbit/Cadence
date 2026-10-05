package com.qyub.mgr2.presentation.screens.timeline

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qyub.mgr2.presentation.screens.timeline.components.CalendarNavigator
import com.qyub.mgr2.presentation.screens.timeline.components.EventSheet
import com.qyub.mgr2.presentation.screens.timeline.components.TimelinePager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    modifier: Modifier = Modifier,
    onOpenMenu: () -> Unit = {},
    onEventCreate: () -> Unit = {},
    onEventEdit: (Int) -> Unit = {},
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var calendarOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = onEventCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Text("+", fontSize = 30.sp, fontWeight = FontWeight.Light)
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("${uiState.displayDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} — ${formatDayOfWeek(uiState.displayDay)}")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onOpenMenu
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            calendarOpen = true
                        }
                    ) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Choose date")
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
                    navigationIconContentColor = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
                    subtitleContentColor = TopAppBarDefaults.topAppBarColors().subtitleContentColor
                ),
            )
        }
    ) { padding ->
        TimelinePager(
            modifier = Modifier
                .padding(padding)
                .padding(start = 16.dp),
            uiState = uiState,
            onDayChange = { viewModel.setDay(it) },
            onEventClick = { viewModel.setInspectedEvent(it) }
        )

        if (calendarOpen) {
            CalendarNavigator(
                initialDate = uiState.displayDay,
                onConfirm = { date ->
                    viewModel.setDay(date)
                    calendarOpen = false
                }
            )
        }

        if (uiState.inspectedEvent != null) {
            EventSheet(
                event = uiState.inspectedEvent!!,
                onDismiss = {
                    viewModel.setInspectedEvent(null)
                },
                onEdit = {
                    onEventEdit(uiState.inspectedEvent!!.id)
                },
                onDelete = {
                    viewModel.onEventDelete(uiState.inspectedEvent!!)
                    viewModel.setInspectedEvent(null)
                }
            )
        }
    }
}

private fun formatDayOfWeek(date: LocalDate): String {
    return date.dayOfWeek.name.substring(0, 2).lowercase().replaceFirstChar { it.uppercase() }
}