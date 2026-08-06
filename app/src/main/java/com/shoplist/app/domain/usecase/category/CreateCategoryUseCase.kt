package com.shoplist.app.domain.usecase.category

import com.shoplist.app.domain.repository.CategoryRepository
import javax.inject.Inject

class CreateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(name: String): Long = repository.createCategory(name)
}
