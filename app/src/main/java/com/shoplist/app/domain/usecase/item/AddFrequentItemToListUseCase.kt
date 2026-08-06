package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import javax.inject.Inject

class AddFrequentItemToListUseCase @Inject constructor(
    private val repository: ShoppingListItemRepository
) {
    suspend operator fun invoke(listId: Long, product: FrequentProduct): Long =
        repository.addItem(
            listId = listId,
            productId = product.productId,
            quantity = 1.0,
            unit = product.defaultUnit,
            note = null
        )
}
