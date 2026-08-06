package com.shoplist.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shoplist.app.data.local.AppDatabase
import com.shoplist.app.data.local.MIGRATION_1_2
import com.shoplist.app.data.local.dao.CategoryDao
import com.shoplist.app.data.local.dao.ProductDao
import com.shoplist.app.data.local.dao.ShoppingListDao
import com.shoplist.app.data.local.dao.ShoppingListItemDao
import com.shoplist.app.data.local.seedInitialData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<AppDatabase>
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "shop-list.db")
            .addMigrations(MIGRATION_1_2)
            .addCallback(SeedDatabaseCallback(databaseProvider))
            .build()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideShoppingListDao(database: AppDatabase): ShoppingListDao = database.shoppingListDao()

    @Provides
    fun provideShoppingListItemDao(database: AppDatabase): ShoppingListItemDao = database.shoppingListItemDao()
}

/**
 * [databaseProvider] is injected lazily (Dagger `Provider`) specifically so this callback
 * can be constructed inside [provideAppDatabase] itself without a circular-dependency error:
 * `.get()` is only called from [onCreate], well after the database singleton exists.
 */
private class SeedDatabaseCallback(
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            seedInitialData(databaseProvider.get())
        }
    }
}
