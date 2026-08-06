package com.shoplist.app.domain.usecase.product

import com.shoplist.app.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteProduct(id)
}
