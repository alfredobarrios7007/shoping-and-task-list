package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.repository.ShoppingListRepository
import javax.inject.Inject

class RenameShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(id: Long, name: String) = repository.renameList(id, name)
}
