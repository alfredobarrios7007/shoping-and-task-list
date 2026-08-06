package com.shoplist.app.data.local

import com.shoplist.app.data.local.entity.CategoryEntity
import com.shoplist.app.data.local.entity.ProductEntity
import com.shoplist.app.data.local.entity.ShoppingListEntity
import com.shoplist.app.data.local.entity.ShoppingListItemEntity
import com.shoplist.app.domain.model.Priority

/**
 * Populates a freshly created database with a starter catalog of categories/products
 * and a handful of ready-to-use shopping lists. Only runs once, from
 * [androidx.room.RoomDatabase.Callback.onCreate] (see [com.shoplist.app.di.DatabaseModule]),
 * so it never touches an existing user database.
 */
internal suspend fun seedInitialData(database: AppDatabase) {
    val categoryDao = database.categoryDao()
    val productDao = database.productDao()
    val shoppingListDao = database.shoppingListDao()
    val itemDao = database.shoppingListItemDao()
    val now = System.currentTimeMillis()

    suspend fun category(name: String) = categoryDao.insert(CategoryEntity(name = name, createdAt = now))
    suspend fun product(name: String, categoryId: Long) =
        productDao.insert(ProductEntity(name = name, categoryId = categoryId, defaultUnit = null, createdAt = now))

    val supermarket = category("Supermarket")
    val stationery = category("Stationery")
    val diyAndTools = category("DIY and tools")
    val workTasks = category("Work tasks")
    val homework = category("Homework")
    val diy = category("DIY")
    val upperBody = category("Upper Body Workout routine")
    val lowerBody = category("Lower Body Workout routine")
    val habitTracker = category("Habit tracker")

    val milk = product("Milk", supermarket)
    val oatMilk = product("Oat milk", supermarket)
    val ham = product("Ham", supermarket)
    val freshCheese = product("Fresh cheese", supermarket)
    product("Manchego cheese", supermarket)
    product("Goat cheese", supermarket)
    product("Bananas", supermarket)
    product("Apples", supermarket)
    product("Strawberries", supermarket)
    product("Blueberries", supermarket)
    product("Water", supermarket)
    val bread = product("Bread", supermarket)
    product("Tortillas", supermarket)
    product("Ice", supermarket)
    product("Spinaches", supermarket)
    product("Garrots", supermarket)
    product("Oranges", supermarket)
    val yogur = product("Yogur", supermarket)

    product("Post-it", stationery)
    product("Notebook", stationery)
    product("Black pen", stationery)
    product("Blue pen", stationery)
    product("Red pen", stationery)
    product("Green pen", stationery)

    product("Hamer", diyAndTools)
    product("Nails", diyAndTools)

    product("Identify REST API Services", workTasks)
    product("Create DB tables", workTasks)
    product("Create the UI", workTasks)
    product("Create the services", workTasks)

    product("Homework 1", homework)
    product("Homework 2", homework)

    product("Sweep", diy)
    product("Mop", diy)
    product("Cook", diy)

    product("Pull-ups", upperBody)
    product("Push-ups", upperBody)
    product("Dips", upperBody)
    product("Arm curl", upperBody)
    product("Wrist curl", upperBody)
    product("Military press", upperBody)
    product("Hold weight", upperBody)

    product("Squats", lowerBody)
    product("Lunges", lowerBody)
    product("Legs press", lowerBody)
    product("Heel raise", lowerBody)

    val moveArms = product("Move arms", habitTracker)
    val kneeRaise = product("Knee raise", habitTracker)
    val drinkLemonWater = product("Drink lemon water", habitTracker)
    val readEnglishBook = product("Read English language book", habitTracker)
    val floorWorkout = product("Floor workout", habitTracker)
    val breakfast = product("Breakfast", habitTracker)
    val work = product("Work", habitTracker)
    val drinkCoffee = product("Drink coffee", habitTracker)
    val eatChocolate = product("Eat chocolate", habitTracker)
    val lunch = product("Lunch", habitTracker)
    val upperBodyTask = product("Upper Body Workout routine", habitTracker)
    val lowerBodyTask = product("Lower Body Workout routine", habitTracker)
    val study = product("Study", habitTracker)
    val studyFinances = product("Study finances", habitTracker)
    val studyCoding = product("Study coding", habitTracker)
    val read = product("Read", habitTracker)
    val sleep = product("Sleep", habitTracker)
    val rest = product("Rest", habitTracker)

    suspend fun list(name: String, productIds: List<Long>) {
        val listId = shoppingListDao.insert(ShoppingListEntity(name = name, createdAt = now, updatedAt = now))
        itemDao.insertAll(
            productIds.map { productId ->
                ShoppingListItemEntity(
                    listId = listId,
                    productId = productId,
                    quantity = 1.0,
                    unit = null,
                    note = null,
                    priority = Priority.NORMAL,
                    addedAt = now
                )
            }
        )
    }

    list("Frequent supermarket", listOf(milk, oatMilk, ham, freshCheese, bread, yogur))

    list(
        "Monday",
        listOf(
            moveArms, kneeRaise, drinkLemonWater, readEnglishBook, breakfast, work, drinkCoffee,
            eatChocolate, lunch, upperBodyTask, work, studyFinances, studyCoding, read, sleep
        )
    )
    list(
        "Tuesday",
        listOf(
            moveArms, kneeRaise, drinkLemonWater, readEnglishBook, floorWorkout, breakfast, work,
            drinkCoffee, eatChocolate, lunch, lowerBodyTask, work, studyFinances, studyCoding, read, sleep
        )
    )
    list(
        "Wednesday",
        listOf(
            moveArms, kneeRaise, drinkLemonWater, readEnglishBook, floorWorkout, breakfast, work,
            drinkCoffee, eatChocolate, lunch, work, studyFinances, studyCoding, read, sleep
        )
    )
    list(
        "Thursday",
        listOf(
            moveArms, kneeRaise, drinkLemonWater, readEnglishBook, floorWorkout, breakfast, work,
            drinkCoffee, eatChocolate, lunch, upperBodyTask, work, studyFinances, studyCoding, read, sleep
        )
    )
    list(
        "Friday",
        listOf(
            moveArms, kneeRaise, drinkLemonWater, readEnglishBook, floorWorkout, breakfast, work,
            drinkCoffee, eatChocolate, lunch, lowerBodyTask, work, studyFinances, studyCoding, read, sleep
        )
    )
    list("Saturday", listOf(breakfast, study, lunch, rest))
    list("Sunday", listOf(breakfast, lunch, rest))
}
