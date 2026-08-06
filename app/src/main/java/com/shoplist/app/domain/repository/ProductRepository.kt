package com.shoplist.app.domain.repository

import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>>
    suspend fun createProduct(name: String, categoryId: Long, defaultUnit: String?): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Long)
    fun getFrequentProducts(since: Long, limit: Int): Flow<List<FrequentProduct>>
}
