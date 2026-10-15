package com.qyub.mgr2.presentation.screens.timeline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qyub.mgr2.domain.model.Occurrence
import com.qyub.mgr2.presentation.screens.event.components.DeleteOptionDialog
import com.qyub.mgr2.presentation.screens.timeline.EventUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSheet(
    event: EventUIState,
    onDismiss: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDeleteOccurrence: () -> Unit = {},
    onDeleteSeries: () -> Unit = {},
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showDeleteOptionDialog by remember { mutableStateOf(false) }

    val isRepeating = event.taskRef.occurrence !is Occurrence.FixedDate || event.taskRef.exceptionInfo != null

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(Icons.Default.Close, "Close")
                }

                Row {
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(Icons.Default.Edit, "Edit")
                    }

                    IconButton(
                        onClick = {
                            if (isRepeating) {
                                showDeleteOptionDialog = true
                            } else {
                                onDeleteOccurrence()
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            Text(event.taskRef.title, style = MaterialTheme.typography.titleMedium)
        }
    }

    if (showDeleteOptionDialog) {
        DeleteOptionDialog(
            onConfirm = { deleteIndividually ->
                showDeleteOptionDialog = false
                if (deleteIndividually) {
                    onDeleteOccurrence()
                } else {
                    onDeleteSeries()
                }
                onDismiss()
            },
            onDismiss = {
                showDeleteOptionDialog = false
            }
        )
    }
}