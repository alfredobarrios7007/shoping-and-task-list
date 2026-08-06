package com.shoplist.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shoplist.app.data.local.dao.CategoryDao
import com.shoplist.app.data.local.dao.ProductDao
import com.shoplist.app.data.local.dao.ShoppingListDao
import com.shoplist.app.data.local.dao.ShoppingListItemDao
import com.shoplist.app.data.local.entity.CategoryEntity
import com.shoplist.app.data.local.entity.ProductEntity
import com.shoplist.app.data.local.entity.ShoppingListEntity
import com.shoplist.app.data.local.entity.ShoppingListItemEntity

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        ShoppingListEntity::class,
        ShoppingListItemEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao
}
