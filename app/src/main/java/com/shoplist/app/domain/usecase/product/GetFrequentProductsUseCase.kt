package com.shoplist.app.domain.usecase.product

import com.shoplist.app.domain.model.FrequentProduct
import com.shoplist.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetFrequentProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(now: Long, limit: Int = 10): Flow<List<FrequentProduct>> {
        val since = now - TimeUnit.DAYS.toMillis(RECENCY_WINDOW_DAYS)
        return repository.getFrequentProducts(since, limit)
    }

    private companion object {
        const val RECENCY_WINDOW_DAYS = 90L
    }
}
