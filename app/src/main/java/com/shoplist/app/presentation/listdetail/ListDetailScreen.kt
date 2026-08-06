package com.shoplist.app.presentation.listdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoplist.app.R
import com.shoplist.app.presentation.common.EmptyState
import com.shoplist.app.presentation.common.LoadingIndicator
import com.shoplist.app.presentation.listdetail.components.AddItemSheet
import com.shoplist.app.presentation.listdetail.components.CategoryHeader
import com.shoplist.app.presentation.listdetail.components.FrequentChipsRow
import com.shoplist.app.presentation.listdetail.components.ItemRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    onBack: () -> Unit,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddItemSheet by remember { mutableStateOf(false) }
    var collapsedCategoryIds by remember { mutableStateOf(setOf<Long>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.listName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_add_item))
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(innerPadding))
            uiState.groups.isEmpty() -> EmptyState(
                message = stringResource(R.string.list_detail_empty),
                modifier = Modifier.padding(innerPadding)
            )
            else -> Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                FrequentChipsRow(
                    products = uiState.frequentProducts,
                    onProductClick = viewModel::onAddFrequentProduct
                )
                LazyColumn {
                    uiState.groups.forEach { group ->
                        val expanded = group.categoryId !in collapsedCategoryIds
                        item(key = "header_${group.categoryId}") {
                            CategoryHeader(
                                categoryName = group.categoryName,
                                itemCount = group.items.size,
                                expanded = expanded,
                                onToggleExpanded = {
                                    collapsedCategoryIds = if (expanded) {
                                        collapsedCategoryIds + group.categoryId
                                    } else {
                                        collapsedCategoryIds - group.categoryId
                                    }
                                }
                            )
                        }
                        if (expanded) {
                            items(group.items, key = { it.id }) { item ->
                                ItemRow(
                                    item = item,
                                    onToggleChecked = { checked -> viewModel.onToggleChecked(item.id, checked) },
                                    onQuantityChange = { quantity -> viewModel.onUpdateQuantity(item, quantity) },
                                    onCyclePriority = { viewModel.onCyclePriority(item) },
                                    onRemove = { viewModel.onRemoveItem(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddItemSheet) {
        AddItemSheet(
            products = uiState.allProducts,
            categories = uiState.categories,
            onSelectProduct = viewModel::onAddExistingProduct,
            onCreateProduct = viewModel::onCreateAndAddProduct,
            onDismiss = { showAddItemSheet = false }
        )
    }
}
