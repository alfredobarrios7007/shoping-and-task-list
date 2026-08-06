package com.shoplist.app.data.repository

import com.shoplist.app.data.local.dao.CategoryDao
import com.shoplist.app.data.local.entity.CategoryEntity
import com.shoplist.app.data.mapper.toDomain
import com.shoplist.app.domain.model.Category
import com.shoplist.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        categoryDao.getCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun createCategory(name: String): Long =
        categoryDao.insert(CategoryEntity(name = name, createdAt = System.currentTimeMillis()))

    override suspend fun renameCategory(id: Long, name: String) = categoryDao.rename(id, name)

    override suspend fun deleteCategory(id: Long) = categoryDao.delete(id)
}
