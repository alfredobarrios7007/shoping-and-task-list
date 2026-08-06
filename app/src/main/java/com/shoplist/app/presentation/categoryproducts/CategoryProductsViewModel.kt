package com.shoplist.app.presentation.categoryproducts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.usecase.product.CreateProductUseCase
import com.shoplist.app.domain.usecase.product.DeleteProductUseCase
import com.shoplist.app.domain.usecase.product.GetProductsByCategoryUseCase
import com.shoplist.app.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProductsByCategory: GetProductsByCategoryUseCase,
    private val createProduct: CreateProductUseCase,
    private val deleteProduct: DeleteProductUseCase
) : ViewModel() {

    val categoryId: Long = checkNotNull(savedStateHandle[Routes.CategoryProducts.ARG_CATEGORY_ID])

    val uiState: StateFlow<CategoryProductsUiState> = getProductsByCategory(categoryId)
        .map { products -> CategoryProductsUiState(products = products, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryProductsUiState())

    fun onCreateProduct(name: String, defaultUnit: String?) {
        if (name.isBlank()) return
        viewModelScope.launch { createProduct(name.trim(), categoryId, defaultUnit?.trim()?.ifBlank { null }) }
    }

    fun onDeleteProduct(id: Long) {
        viewModelScope.launch { deleteProduct(id) }
    }
}
