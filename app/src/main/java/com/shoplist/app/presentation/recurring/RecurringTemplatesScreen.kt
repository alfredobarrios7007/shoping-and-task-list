package com.shoplist.app.presentation.recurring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoplist.app.R
import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.presentation.common.ConfirmDeleteDialog
import com.shoplist.app.presentation.common.EmptyState
import com.shoplist.app.presentation.common.LoadingIndicator
import com.shoplist.app.presentation.common.displayName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTemplatesScreen(
    viewModel: RecurringTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var templateToEdit by remember { mutableStateOf<ShoppingList?>(null) }
    var templateToDelete by remember { mutableStateOf<ShoppingList?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.recurring_lists_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_new_recurring_list))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.templates.isEmpty() -> EmptyState(
                message = stringResource(R.string.recurring_empty),
                modifier = Modifier.padding(innerPadding)
            )
            else -> LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(uiState.templates, key = { it.id }) { template ->
                    RecurringTemplateRow(
                        template = template,
                        onEditInterval = { templateToEdit = template },
                        onTurnOff = { viewModel.onPause(template) },
                        onDelete = { templateToDelete = template }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        NewTemplateDialog(
            onConfirm = { name, interval ->
                viewModel.onCreateTemplate(name, interval)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    templateToEdit?.let { template ->
        IntervalPickerDialog(
            title = stringResource(R.string.change_interval_dialog_title, template.name),
            initialInterval = template.recurrenceInterval ?: RecurrenceInterval.WEEKLY,
            onConfirm = { interval ->
                viewModel.onChangeInterval(template, interval)
                templateToEdit = null
            },
            onDismiss = { templateToEdit = null }
        )
    }

    templateToDelete?.let { template ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.delete_template_dialog_title, template.name),
            message = stringResource(R.string.delete_template_dialog_message),
            onConfirm = {
                viewModel.onDelete(template)
                templateToDelete = null
            },
            onDismiss = { templateToDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringTemplateRow(
    template: ShoppingList,
    onEditInterval: () -> Unit,
    onTurnOff: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    ListItem(
        headlineContent = { Text(template.name) },
        supportingContent = {
            val interval = template.recurrenceInterval?.displayName()
            val nextDue = template.nextDueAt?.let { dateFormat.format(Date(it)) } ?: stringResource(R.string.not_scheduled)
            Text(stringResource(R.string.recurring_next_due, interval.orEmpty(), nextDue))
        },
        modifier = Modifier.fillMaxWidth(),
        trailingContent = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.cd_more_options))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_change_interval)) }, onClick = { menuExpanded = false; onEditInterval() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_turn_off_recurring)) }, onClick = { menuExpanded = false; onTurnOff() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_delete)) }, onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    )
}

@Composable
private fun NewTemplateDialog(
    onConfirm: (name: String, interval: RecurrenceInterval) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf(RecurrenceInterval.WEEKLY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_recurring_list_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text(stringResource(R.string.label_name)) })
                IntervalRadioGroup(selected = interval, onSelect = { interval = it })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, interval) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_create)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
private fun IntervalPickerDialog(
    title: String,
    initialInterval: RecurrenceInterval,
    onConfirm: (RecurrenceInterval) -> Unit,
    onDismiss: () -> Unit
) {
    var interval by remember { mutableStateOf(initialInterval) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { IntervalRadioGroup(selected = interval, onSelect = { interval = it }) },
        confirmButton = {
            TextButton(onClick = { onConfirm(interval) }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
private fun IntervalRadioGroup(
    selected: RecurrenceInterval,
    onSelect: (RecurrenceInterval) -> Unit
) {
    Column {
        RecurrenceInterval.entries.forEach { interval ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(selected = selected == interval, onClick = { onSelect(interval) })
                Text(interval.displayName())
            }
        }
    }
}
