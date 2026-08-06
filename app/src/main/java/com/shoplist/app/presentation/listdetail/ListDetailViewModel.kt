package com.shoplist.app.presentation.listdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoplist.app.domain.model.Category
import com.shoplist.app.domain.model.CategoryItemGroup
import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.model.ShoppingListItem
import com.shoplist.app.domain.model.next
import com.shoplist.app.domain.usecase.category.GetCategoriesUseCase
import com.shoplist.app.domain.usecase.item.AddFrequentItemToListUseCase
import com.shoplist.app.domain.usecase.item.AddItemToListUseCase
import com.shoplist.app.domain.usecase.item.GetItemsForListUseCase
import com.shoplist.app.domain.usecase.item.GetListItemsGroupedByCategoryUseCase
import com.shoplist.app.domain.usecase.item.RemoveShoppingListItemUseCase
import com.shoplist.app.domain.usecase.item.ToggleItemCheckedUseCase
import com.shoplist.app.domain.usecase.item.UpdateShoppingListItemUseCase
import com.shoplist.app.domain.usecase.list.GetShoppingListUseCase
import com.shoplist.app.domain.usecase.product.CreateProductUseCase
import com.shoplist.app.domain.usecase.product.GetAllProductsUseCase
import com.shoplist.app.domain.usecase.product.GetFrequentProductsUseCase
import com.shoplist.app.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListDetailUiState(
    val listName: String = "",
    val groups: List<CategoryItemGroup> = emptyList(),
    val frequentProducts: List<FrequentProduct> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getShoppingList: GetShoppingListUseCase,
    getItemsForList: GetItemsForListUseCase,
    private val groupItemsByCategory: GetListItemsGroupedByCategoryUseCase,
    getFrequentProducts: GetFrequentProductsUseCase,
    getAllProducts: GetAllProductsUseCase,
    getCategories: GetCategoriesUseCase,
    private val addItemToList: AddItemToListUseCase,
    private val addFrequentItemToList: AddFrequentItemToListUseCase,
    private val updateShoppingListItem: UpdateShoppingListItemUseCase,
    private val toggleItemChecked: ToggleItemCheckedUseCase,
    private val removeShoppingListItem: RemoveShoppingListItemUseCase,
    private val createProduct: CreateProductUseCase
) : ViewModel() {

    val listId: Long = checkNotNull(savedStateHandle[Routes.ListDetail.ARG_LIST_ID])

    private val productPicker = combine(getAllProducts(), getCategories()) { products, categories ->
        products to categories
    }

    val uiState: StateFlow<ListDetailUiState> = combine(
        getShoppingList(listId),
        getItemsForList(listId).map { groupItemsByCategory(it) },
        getFrequentProducts(System.currentTimeMillis()),
        productPicker
    ) { list, groups, frequent, (products, categories) ->
        ListDetailUiState(
            listName = list?.name.orEmpty(),
            groups = groups,
            frequentProducts = frequent,
            allProducts = products,
            categories = categories,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListDetailUiState())

    fun onToggleChecked(itemId: Long, checked: Boolean) {
        viewModelScope.launch { toggleItemChecked(itemId, checked) }
    }

    fun onRemoveItem(itemId: Long) {
        viewModelScope.launch { removeShoppingListItem(itemId) }
    }

    fun onUpdateQuantity(item: ShoppingListItem, quantity: Double) {
        viewModelScope.launch { updateShoppingListItem(item.copy(quantity = quantity)) }
    }

    fun onCyclePriority(item: ShoppingListItem) {
        viewModelScope.launch { updateShoppingListItem(item.copy(priority = item.priority.next())) }
    }

    fun onAddExistingProduct(product: Product) {
        viewModelScope.launch { addItemToList(listId, product.id, 1.0, product.defaultUnit, null) }
    }

    fun onAddFrequentProduct(product: FrequentProduct) {
        viewModelScope.launch { addFrequentItemToList(listId, product) }
    }

    fun onCreateAndAddProduct(name: String, categoryId: Long, defaultUnit: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val productId = createProduct(name.trim(), categoryId, defaultUnit?.ifBlank { null })
            addItemToList(listId, productId, 1.0, defaultUnit?.ifBlank { null }, null)
        }
    }
}
