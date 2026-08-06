package com.shoplist.app.domain.usecase.product

import com.shoplist.app.domain.repository.ProductRepository
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(name: String, categoryId: Long, defaultUnit: String?): Long =
        repository.createProduct(name, categoryId, defaultUnit)
}
