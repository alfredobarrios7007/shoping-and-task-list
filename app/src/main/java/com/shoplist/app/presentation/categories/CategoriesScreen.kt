package com.shoplist.app.presentation.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoplist.app.R
import com.shoplist.app.domain.model.Category
import com.shoplist.app.presentation.common.AlphabetIndexRail
import com.shoplist.app.presentation.common.ConfirmDeleteDialog
import com.shoplist.app.presentation.common.EmptyState
import com.shoplist.app.presentation.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onCategoryClick: (Category) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToRename by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.categories_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_new_category))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.categories.isEmpty() -> EmptyState(
                message = stringResource(R.string.categories_empty),
                modifier = Modifier.padding(innerPadding)
            )
            else -> {
                val listState = rememberLazyListState()
                Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
                        items(uiState.categories, key = { it.id }) { category ->
                            ListItem(
                                headlineContent = { Text(category.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCategoryClick(category) },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { categoryToRename = category }) {
                                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_rename_category, category.name))
                                        }
                                        IconButton(onClick = { categoryToDelete = category }) {
                                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete_category, category.name))
                                        }
                                    }
                                }
                            )
                        }
                    }
                    AlphabetIndexRail(
                        names = uiState.categories.map { it.name },
                        listState = listState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding(end = 4.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        CategoryNameDialog(
            title = stringResource(R.string.new_category_dialog_title),
            initialName = "",
            onConfirm = { name ->
                viewModel.onCreateCategory(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    categoryToRename?.let { category ->
        CategoryNameDialog(
            title = stringResource(R.string.rename_category_dialog_title),
            initialName = category.name,
            onConfirm = { name ->
                viewModel.onRenameCategory(category.id, name)
                categoryToRename = null
            },
            onDismiss = { categoryToRename = null }
        )
    }

    categoryToDelete?.let { category ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.delete_category_dialog_title, category.name),
            message = stringResource(R.string.delete_category_dialog_message),
            onConfirm = {
                viewModel.onDeleteCategory(category.id)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }
}

@Composable
private fun CategoryNameDialog(
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
