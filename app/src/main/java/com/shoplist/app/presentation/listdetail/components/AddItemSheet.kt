package com.shoplist.app.presentation.listdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shoplist.app.R
import com.shoplist.app.domain.model.Category
import com.shoplist.app.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemSheet(
    products: List<Product>,
    categories: List<Category>,
    onSelectProduct: (Product) -> Unit,
    onCreateProduct: (name: String, categoryId: Long, unit: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    val filtered = remember(products, query) {
        if (query.isBlank()) products else products.filter { it.name.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(R.string.search_products_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(filtered, key = { it.id }) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text(product.categoryName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectProduct(product)
                                onDismiss()
                            }
                    )
                }
                if (categories.isNotEmpty()) {
                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    if (query.isBlank()) stringResource(R.string.create_new_product)
                                    else stringResource(R.string.create_named_product, query)
                                )
                            },
                            leadingContent = { Icon(Icons.Filled.Add, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCreateDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProductDialog(
            initialName = query,
            categories = categories,
            onConfirm = { name, categoryId, unit ->
                onCreateProduct(name, categoryId, unit)
                showCreateDialog = false
                onDismiss()
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun CreateProductDialog(
    initialName: String,
    categories: List<Category>,
    onConfirm: (name: String, categoryId: Long, unit: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var unit by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_product_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text(stringResource(R.string.label_name)) })
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    TextButton(onClick = { categoryMenuExpanded = true }) {
                        Text(stringResource(R.string.category_button_label, selectedCategory.name))
                    }
                    DropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }
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
            TextButton(
                onClick = { onConfirm(name, selectedCategory.id, unit.ifBlank { null }) },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
