package com.shoplist.app.domain.usecase.product

import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(categoryId: Long): Flow<List<Product>> = repository.getProductsByCategory(categoryId)
}
