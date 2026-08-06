package com.shoplist.app.presentation.shoplists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.usecase.list.CloneShoppingListUseCase
import com.shoplist.app.domain.usecase.list.CreateShoppingListUseCase
import com.shoplist.app.domain.usecase.list.DeleteShoppingListUseCase
import com.shoplist.app.domain.usecase.list.GetShoppingListsUseCase
import com.shoplist.app.domain.usecase.list.RenameShoppingListUseCase
import com.shoplist.app.domain.usecase.list.SetListRecurringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListsUiState(
    val lists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ShoppingListsViewModel @Inject constructor(
    getShoppingLists: GetShoppingListsUseCase,
    private val createShoppingList: CreateShoppingListUseCase,
    private val renameShoppingList: RenameShoppingListUseCase,
    private val deleteShoppingList: DeleteShoppingListUseCase,
    private val cloneShoppingList: CloneShoppingListUseCase,
    private val setListRecurring: SetListRecurringUseCase
) : ViewModel() {

    val uiState: StateFlow<ShoppingListsUiState> = getShoppingLists()
        .map { lists -> ShoppingListsUiState(lists = lists, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShoppingListsUiState())

    fun onCreateList(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { createShoppingList(name.trim()) }
    }

    fun onRenameList(id: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { renameShoppingList(id, name.trim()) }
    }

    fun onDeleteList(id: Long) {
        viewModelScope.launch { deleteShoppingList(id) }
    }

    fun onCloneList(list: ShoppingList, newName: String) {
        viewModelScope.launch { cloneShoppingList(list.id, newName) }
    }

    fun onMakeRecurring(list: ShoppingList, interval: RecurrenceInterval) {
        viewModelScope.launch { setListRecurring(list.id, interval, System.currentTimeMillis()) }
    }
}
