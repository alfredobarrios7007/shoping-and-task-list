package com.shoplist.app.domain.usecase.product

import com.shoplist.app.domain.model.Product
import com.shoplist.app.domain.repository.ProductRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) = repository.updateProduct(product)
}
