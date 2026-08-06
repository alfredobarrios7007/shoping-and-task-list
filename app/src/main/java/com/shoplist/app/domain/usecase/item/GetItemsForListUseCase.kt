package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.model.ShoppingListItem
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemsForListUseCase @Inject constructor(
    private val repository: ShoppingListItemRepository
) {
    operator fun invoke(listId: Long): Flow<List<ShoppingListItem>> = repository.getItemsForList(listId)
}
