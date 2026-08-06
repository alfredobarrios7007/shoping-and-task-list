package com.shoplist.app.domain.usecase.category

import com.shoplist.app.domain.repository.CategoryRepository
import javax.inject.Inject

class RenameCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long, name: String) = repository.renameCategory(id, name)
}
