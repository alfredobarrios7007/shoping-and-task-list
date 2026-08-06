package com.shoplist.app.domain.usecase.recurrence

import com.shoplist.app.domain.model.RecurrenceInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class ComputeNextDueDateUseCaseTest {

    private val useCase = ComputeNextDueDateUseCase()

    @Test
    fun `daily interval advances by one day`() {
        val from = calendarOf(2024, Calendar.JANUARY, 15).timeInMillis

        val result = useCase(RecurrenceInterval.DAILY, from)

        val resultCal = Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(Calendar.JANUARY, resultCal.get(Calendar.MONTH))
        assertEquals(16, resultCal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `weekly interval advances by seven days`() {
        val from = calendarOf(2024, Calendar.JANUARY, 15).timeInMillis

        val result = useCase(RecurrenceInterval.WEEKLY, from)

        val resultCal = Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(Calendar.JANUARY, resultCal.get(Calendar.MONTH))
        assertEquals(22, resultCal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `monthly interval rolls into the following month`() {
        val from = calendarOf(2024, Calendar.JANUARY, 15).timeInMillis

        val result = useCase(RecurrenceInterval.MONTHLY, from)

        val resultCal = Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(Calendar.FEBRUARY, resultCal.get(Calendar.MONTH))
        assertEquals(15, resultCal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `advancing from the due timestamp rather than now avoids drift across repeated calls`() {
        var due = calendarOf(2024, Calendar.JANUARY, 1).timeInMillis

        repeat(3) { due = useCase(RecurrenceInterval.WEEKLY, due) }

        val resultCal = Calendar.getInstance().apply { timeInMillis = due }
        assertEquals(Calendar.JANUARY, resultCal.get(Calendar.MONTH))
        assertEquals(22, resultCal.get(Calendar.DAY_OF_MONTH))
    }

    private fun calendarOf(year: Int, month: Int, day: Int): Calendar =
        Calendar.getInstance().apply {
            set(year, month, day, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
}
