package com.qyub.mgr2.ui.screens.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qyub.mgr2.data.models.Event
import com.qyub.mgr2.ui.components.DialogOption
import com.qyub.mgr2.ui.components.EventBottomSheet
import com.qyub.mgr2.ui.components.EventCard
import com.qyub.mgr2.ui.components.OptionDialog
import com.qyub.mgr2.ui.components.PlanningDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    viewModel: InboxViewModel = hiltViewModel(),
    onMenuRequest: () -> Unit
) {
    val events by viewModel.events.collectAsStateWithLifecycle()

    var showSheet by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<Event?>(null) }

    var contextMenuEvent by remember { mutableStateOf<Event?>(null) }
    var planningEvent by remember {mutableStateOf<Event?>(null)}

    val currentDate = LocalDate.now()

    val haptic = LocalHapticFeedback.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventToEdit = null
                    showSheet = true
                },
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
                    Text("Inbox")
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
                    navigationIconContentColor = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onMenuRequest
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                events.forEach { ev ->
                    EventCard(
                        event = ev,
                        onClick = {
                            eventToEdit = it
                            showSheet = true
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            contextMenuEvent = it
                        }
                    )
                }
            }

            contextMenuEvent?.let { ev ->
                OptionDialog(
                    onDismissRequest = { contextMenuEvent = null },
                    title = ev.title,
                    options = listOf(
                        DialogOption("Set Date") {
                            planningEvent = ev
                            contextMenuEvent = null
                        },
                        DialogOption("Edit") {
                            eventToEdit = ev
                            showSheet = true
                        },
                        DialogOption("Delete") {
                            viewModel.deleteEvent(ev)
                            contextMenuEvent = null
                        }
                    ),
                )
            }

            planningEvent?.let { ev ->
                PlanningDialog(
                    eventName = ev.title,
                    onConfirm = { date, time ->
                        planningEvent = null
                    },
                    onDismissRequest = { planningEvent = null }
                )
            }

            if (showSheet) {
                EventBottomSheet(
                    initialEvent = eventToEdit,
                    currentDate = currentDate,
                    isInbox = true,
                    onSave = { input ->
                        if (eventToEdit == null) viewModel.addEvent(input) else viewModel.updateEvent(input)
                        showSheet = false
                    },
                    onDismiss = { showSheet = false },
                    onDelete = { viewModel.deleteEvent(eventToEdit!!) }
                )
            }
        }
    }
}