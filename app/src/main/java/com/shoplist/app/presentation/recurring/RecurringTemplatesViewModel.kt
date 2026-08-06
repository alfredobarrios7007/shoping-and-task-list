package com.shoplist.app.presentation.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.usecase.list.CreateShoppingListUseCase
import com.shoplist.app.domain.usecase.list.DeleteShoppingListUseCase
import com.shoplist.app.domain.usecase.list.GetRecurringTemplatesUseCase
import com.shoplist.app.domain.usecase.list.SetListRecurringUseCase
import com.shoplist.app.domain.usecase.recurrence.ComputeNextDueDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecurringTemplatesUiState(
    val templates: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RecurringTemplatesViewModel @Inject constructor(
    getRecurringTemplates: GetRecurringTemplatesUseCase,
    private val createShoppingList: CreateShoppingListUseCase,
    private val setListRecurring: SetListRecurringUseCase,
    private val deleteShoppingList: DeleteShoppingListUseCase,
    private val computeNextDueDate: ComputeNextDueDateUseCase
) : ViewModel() {

    val uiState: StateFlow<RecurringTemplatesUiState> = getRecurringTemplates()
        .map { templates -> RecurringTemplatesUiState(templates = templates, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecurringTemplatesUiState())

    fun onCreateTemplate(name: String, interval: RecurrenceInterval) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = createShoppingList(name.trim())
            setListRecurring(id, interval, computeNextDueDate(interval, now))
        }
    }

    fun onChangeInterval(template: ShoppingList, interval: RecurrenceInterval) {
        viewModelScope.launch {
            setListRecurring(template.id, interval, computeNextDueDate(interval, System.currentTimeMillis()))
        }
    }

    fun onPause(template: ShoppingList) {
        viewModelScope.launch { setListRecurring(template.id, null, System.currentTimeMillis()) }
    }

    fun onDelete(template: ShoppingList) {
        viewModelScope.launch { deleteShoppingList(template.id) }
    }
}
