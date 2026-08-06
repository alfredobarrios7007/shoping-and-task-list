package com.shoplist.app.domain.usecase.item

import com.shoplist.app.domain.model.CategoryItemGroup
import com.shoplist.app.domain.model.ShoppingListItem
import javax.inject.Inject

class GetListItemsGroupedByCategoryUseCase @Inject constructor() {
    operator fun invoke(items: List<ShoppingListItem>): List<CategoryItemGroup> =
        items
            .groupBy { it.categoryId to it.categoryName }
            .map { (category, groupedItems) ->
                CategoryItemGroup(
                    categoryId = category.first,
                    categoryName = category.second,
                    items = groupedItems
                )
            }
            .sortedBy { it.categoryName }
}
