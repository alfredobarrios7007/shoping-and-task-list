package com.shoplist.app.di

import com.shoplist.app.data.repository.CategoryRepositoryImpl
import com.shoplist.app.data.repository.ProductRepositoryImpl
import com.shoplist.app.data.repository.ShoppingListItemRepositoryImpl
import com.shoplist.app.data.repository.ShoppingListRepositoryImpl
import com.shoplist.app.domain.repository.CategoryRepository
import com.shoplist.app.domain.repository.ProductRepository
import com.shoplist.app.domain.repository.ShoppingListItemRepository
import com.shoplist.app.domain.repository.ShoppingListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindShoppingListRepository(impl: ShoppingListRepositoryImpl): ShoppingListRepository

    @Binds
    @Singleton
    abstract fun bindShoppingListItemRepository(impl: ShoppingListItemRepositoryImpl): ShoppingListItemRepository
}
