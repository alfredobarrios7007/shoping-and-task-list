package com.shoplist.app.domain.model

data class ShoppingList(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isRecurringTemplate: Boolean,
    val recurrenceInterval: RecurrenceInterval?,
    val nextDueAt: Long?,
    val lastGeneratedAt: Long?,
    val clonedFromListId: Long?,
    val itemCount: Int = 0,
    val checkedItemCount: Int = 0
)
