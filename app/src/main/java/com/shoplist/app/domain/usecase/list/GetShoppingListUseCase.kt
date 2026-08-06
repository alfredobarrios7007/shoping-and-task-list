package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.model.ShoppingList
import com.shoplist.app.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    operator fun invoke(id: Long): Flow<ShoppingList?> = repository.getList(id)
}
