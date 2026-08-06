package com.shoplist.app.data.repository

import com.shoplist.app.data.local.dao.ProductDao
import com.shoplist.app.data.local.entity.ProductEntity
import com.shoplist.app.data.mapper.toDomain
import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { list -> list.map { it.toDomain() } }

    override fun getProductsByCategory(categoryId: Long): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    override suspend fun createProduct(name: String, categoryId: Long, defaultUnit: String?): Long =
        productDao.insert(
            ProductEntity(
                name = name,
                categoryId = categoryId,
                defaultUnit = defaultUnit,
                createdAt = System.currentTimeMillis()
            )
        )

    override suspend fun updateProduct(product: Product) =
        productDao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                categoryId = product.categoryId,
                defaultUnit = product.defaultUnit,
                createdAt = product.createdAt
            )
        )

    override suspend fun deleteProduct(id: Long) = productDao.delete(id)

    override fun getFrequentProducts(since: Long, limit: Int): Flow<List<FrequentProduct>> =
        productDao.getFrequentProducts(since, limit).map { list -> list.map { it.toDomain() } }
}
