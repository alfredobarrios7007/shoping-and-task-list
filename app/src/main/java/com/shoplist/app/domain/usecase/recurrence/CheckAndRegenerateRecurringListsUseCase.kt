package com.shoplist.app.domain.usecase.recurrence

import com.shoplist.app.domain.repository.ShoppingListRepository
import javax.inject.Inject

class CheckAndRegenerateRecurringListsUseCase @Inject constructor(
    private val repository: ShoppingListRepository,
    private val computeNextDueDate: ComputeNextDueDateUseCase
) {
    suspend operator fun invoke(now: Long): Int {
        val dueTemplates = repository.getDueTemplates(now)
        for (template in dueTemplates) {
            val interval = template.recurrenceInterval ?: continue
            val dueAt = template.nextDueAt ?: continue
            repository.regenerateFromTemplate(
                templateId = template.id,
                newListName = template.name,
                newNextDueAt = computeNextDueDate(interval, dueAt),
                generatedAt = now
            )
        }
        return dueTemplates.size
    }
}
