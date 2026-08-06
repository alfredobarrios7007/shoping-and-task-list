package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.model.Priority
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import javax.inject.Inject

class AddItemToListUseCase @Inject constructor(
    private val repository: ShoppingListItemRepository
) {
    suspend operator fun invoke(
        listId: Long,
        productId: Long,
        quantity: Double,
        unit: String?,
        note: String?,
        priority: Priority = Priority.NORMAL
    ): Long = repository.addItem(listId, productId, quantity, unit, note, priority)
}
