package com.shoplist.app.domain.usecase.category

import com.shoplist.app.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteCategory(id)
}
