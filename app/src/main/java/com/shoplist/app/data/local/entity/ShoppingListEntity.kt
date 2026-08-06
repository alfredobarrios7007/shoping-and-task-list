package com.shoplist.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shoplist.app.domain.model.RecurrenceInterval

@Entity(
    tableName = "shopping_list",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["clonedFromListId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["clonedFromListId"])]
)
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isRecurringTemplate: Boolean = false,
    val recurrenceInterval: RecurrenceInterval? = null,
    val nextDueAt: Long? = null,
    val lastGeneratedAt: Long? = null,
    val clonedFromListId: Long? = null
)
