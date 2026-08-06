package com.shoplist.app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoplist.app.domain.model.Category
import com.shoplist.app.domain.usecase.category.CreateCategoryUseCase
import com.shoplist.app.domain.usecase.category.DeleteCategoryUseCase
import com.shoplist.app.domain.usecase.category.GetCategoriesUseCase
import com.shoplist.app.domain.usecase.category.RenameCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    getCategories: GetCategoriesUseCase,
    private val createCategory: CreateCategoryUseCase,
    private val renameCategory: RenameCategoryUseCase,
    private val deleteCategory: DeleteCategoryUseCase
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = getCategories()
        .map { categories -> CategoriesUiState(categories = categories, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriesUiState())

    fun onCreateCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { createCategory(name.trim()) }
    }

    fun onRenameCategory(id: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { renameCategory(id, name.trim()) }
    }

    fun onDeleteCategory(id: Long) {
        viewModelScope.launch { deleteCategory(id) }
    }
}
