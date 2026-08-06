package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShoppingListsUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    operator fun invoke(): Flow<List<ShoppingList>> = repository.getActiveLists()
}
