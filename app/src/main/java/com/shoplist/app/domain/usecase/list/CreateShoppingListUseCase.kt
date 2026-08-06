package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.repository.ShoppingListRepository
import javax.inject.Inject

class CreateShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(name: String): Long = repository.createList(name)
}
