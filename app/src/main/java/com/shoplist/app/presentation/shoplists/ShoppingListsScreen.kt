package com.shoplist.app.presentation.shoplists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(
    onListClick: (ShoppingList) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ShoppingListsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var listToRename by remember { mutableStateOf<ShoppingList?>(null) }
    var listToDelete by remember { mutableStateOf<ShoppingList?>(null) }
    var listToMakeRecurring by remember { mutableStateOf<ShoppingList?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_lists_title)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.cd_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_new_list))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.lists.isEmpty() -> EmptyState(
                message = stringResource(R.string.shopping_lists_empty),
                modifier = Modifier.padding(innerPadding)
            )
            else -> LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(uiState.lists, key = { it.id }) { list ->
                    val cloneName = stringResource(R.string.clone_name_suffix, list.name)
                    ShoppingListRow(
                        list = list,
                        onClick = { onListClick(list) },
                        onRename = { listToRename = list },
                        onDelete = { listToDelete = list },
                        onClone = { viewModel.onCloneList(list, cloneName) },
                        onMakeRecurring = { listToMakeRecurring = list }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ListNameDialog(
            title = stringResource(R.string.new_list_dialog_title),
            initialName = "",
            onConfirm = { name ->
                viewModel.onCreateList(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    listToRename?.let { list ->
        ListNameDialog(
            title = stringResource(R.string.rename_list_dialog_title),
            initialName = list.name,
            onConfirm = { name ->
                viewModel.onRenameList(list.id, name)
                listToRename = null
            },
            onDismiss = { listToRename = null }
        )
    }

    listToDelete?.let { list ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.delete_list_dialog_title, list.name),
            message = stringResource(R.string.delete_list_dialog_message),
            onConfirm = {
                viewModel.onDeleteList(list.id)
                listToDelete = null
            },
            onDismiss = { listToDelete = null }
        )
    }

    listToMakeRecurring?.let { list ->
        RecurrenceIntervalDialog(
            listName = list.name,
            onConfirm = { interval ->
                viewModel.onMakeRecurring(list, interval)
                listToMakeRecurring = null
            },
            onDismiss = { listToMakeRecurring = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListRow(
    list: ShoppingList,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onClone: () -> Unit,
    onMakeRecurring: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(list.name) },
        supportingContent = {
            if (list.itemCount > 0) {
                Column {
                    Text(stringResource(R.string.items_checked_progress, list.checkedItemCount, list.itemCount))
                    LinearProgressIndicator(
                        progress = { list.checkedItemCount.toFloat() / list.itemCount },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Text(stringResource(R.string.no_items_yet))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        trailingContent = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.cd_more_options))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_rename)) }, onClick = { menuExpanded = false; onRename() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_clone)) }, onClick = { menuExpanded = false; onClone() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_make_recurring)) }, onClick = { menuExpanded = false; onMakeRecurring() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_delete)) }, onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    )
}

@Composable
private fun ListNameDialog(
    title: String,
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Composable
private fun RecurrenceIntervalDialog(
    listName: String,
    onConfirm: (RecurrenceInterval) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(RecurrenceInterval.WEEKLY) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.make_recurring_dialog_title, listName)) },
        text = {
            Column {
                RecurrenceInterval.entries.forEach { interval ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = interval }
                    ) {
                        RadioButton(selected = selected == interval, onClick = { selected = interval })
                        Text(interval.displayName())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
