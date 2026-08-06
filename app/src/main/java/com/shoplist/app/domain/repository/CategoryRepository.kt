package com.shoplist.app.domain.repository

import com.shoplist.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun createCategory(name: String): Long
    suspend fun renameCategory(id: Long, name: String)
    suspend fun deleteCategory(id: Long)
}
