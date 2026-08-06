package com.shoplist.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shoplist.app.domain.model.Priority

@Entity(
    tableName = "shopping_list_item",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["listId"]),
        Index(value = ["productId"])
    ]
)
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val productId: Long,
    val quantity: Double,
    val unit: String?,
    val note: String?,
    val isChecked: Boolean = false,
    val priority: Priority = Priority.NORMAL,
    val addedAt: Long
)
