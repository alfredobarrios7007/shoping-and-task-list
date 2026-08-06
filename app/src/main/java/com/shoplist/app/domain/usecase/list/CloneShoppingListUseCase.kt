package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.repository.ShoppingListRepository
import javax.inject.Inject

/**
 * Used by the manual "Clone list" UI action. The recurring-regeneration path
 * ([com.shoplist.app.domain.usecase.recurrence.CheckAndRegenerateRecurringListsUseCase])
 * does not call this directly — it needs the clone plus the next-due advance to
 * commit as one atomic transaction, so it goes through
 * [com.shoplist.app.domain.repository.ShoppingListRepository.regenerateFromTemplate]
 * instead. Both paths share the same underlying clone routine in the repository
 * implementation, so the copy logic itself still exists exactly once.
 */
class CloneShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(sourceListId: Long, newListName: String): Long =
        repository.cloneList(sourceListId, newListName)
}
