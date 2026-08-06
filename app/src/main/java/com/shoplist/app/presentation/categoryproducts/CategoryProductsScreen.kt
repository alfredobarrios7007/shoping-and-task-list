package com.shoplist.app.presentation.categoryproducts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.shoplist.app.domain.model.Product
import com.shoplist.app.presentation.common.AlphabetIndexRail
import com.shoplist.app.presentation.common.ConfirmDeleteDialog
import com.shoplist.app.presentation.common.EmptyState
import com.shoplist.app.presentation.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    categoryName: String,
    onBack: () -> Unit,
    viewModel: CategoryProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_new_product))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.products.isEmpty() -> EmptyState(
                message = stringResource(R.string.category_products_empty),
                modifier = Modifier.padding(innerPadding)
            )
            else -> {
                val listState = rememberLazyListState()
                Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
                        items(uiState.products, key = { it.id }) { product ->
                            ListItem(
                                headlineContent = { Text(product.name) },
                                supportingContent = product.defaultUnit?.let { { Text(it) } },
                                modifier = Modifier.fillMaxWidth(),
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { productToDelete = product }) {
                                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete_product, product.name))
                                        }
                                    }
                                }
                            )
                        }
                    }
                    AlphabetIndexRail(
                        names = uiState.products.map { it.name },
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
        AddProductDialog(
            onConfirm = { name, unit ->
                viewModel.onCreateProduct(name, unit)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    productToDelete?.let { product ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.delete_product_dialog_title, product.name),
            message = stringResource(R.string.delete_product_dialog_message),
            onConfirm = {
                viewModel.onDeleteProduct(product.id)
                productToDelete = null
            },
            onDismiss = { productToDelete = null }
        )
    }
}

@Composable
private fun AddProductDialog(
    onConfirm: (name: String, unit: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_product_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text(stringResource(R.string.label_name)) })
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.label_default_unit_optional)) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, unit.ifBlank { null }) }, enabled = name.isNotBlank()) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
