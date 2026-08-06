package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.repository.ShoppingListItemRepository
import javax.inject.Inject

class ToggleItemCheckedUseCase @Inject constructor(
    private val repository: ShoppingListItemRepository
) {
    suspend operator fun invoke(itemId: Long, checked: Boolean) = repository.setChecked(itemId, checked)
}
