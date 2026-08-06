package com.shoplist.app.domain.usecase.recurrence

import com.shoplist.app.domain.model.RecurrenceInterval
import java.util.Calendar
import javax.inject.Inject

class ComputeNextDueDateUseCase @Inject constructor() {
    operator fun invoke(interval: RecurrenceInterval, from: Long): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = from }
        when (interval) {
            RecurrenceInterval.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            RecurrenceInterval.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceInterval.MONTHLY -> calendar.add(Calendar.MONTH, 1)
        }
        return calendar.timeInMillis
    }
}
