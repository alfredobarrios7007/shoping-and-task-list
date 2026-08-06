package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.repository.ShoppingListRepository
import javax.inject.Inject

class DeleteShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteList(id)
}
