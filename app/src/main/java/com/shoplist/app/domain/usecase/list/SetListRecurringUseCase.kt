package com.shoplist.app.domain.usecase.list

import com.shoplist.app.domain.model.RecurrenceInterval
import com.shoplist.app.domain.repository.ShoppingListRepository
import com.shoplist.app.domain.usecase.recurrence.ComputeNextDueDateUseCase
import javax.inject.Inject

class SetListRecurringUseCase @Inject constructor(
    private val repository: ShoppingListRepository,
    private val computeNextDueDate: ComputeNextDueDateUseCase
) {
    suspend operator fun invoke(id: Long, interval: RecurrenceInterval?, now: Long) {
        val nextDueAt = interval?.let { computeNextDueDate(it, now) }
        repository.setRecurring(id, interval, nextDueAt)
    }
}
