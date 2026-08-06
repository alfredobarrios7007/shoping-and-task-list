package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.model.ShoppingListItem
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import javax.inject.Inject

class UpdateShoppingListItemUseCase @Inject constructor(
    private val repository: ShoppingListItemRepository
) {
    suspend operator fun invoke(item: ShoppingListItem) = repository.updateItem(item)
}
