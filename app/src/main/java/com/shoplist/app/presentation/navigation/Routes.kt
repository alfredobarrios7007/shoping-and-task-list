package com.shoplist.app.presentation.navigation

import android.net.Uri

sealed class Routes(val route: String) {
    data object ShoppingLists : Routes("lists")
    data object ListDetail : Routes("lists/{listId}") {
        const val ARG_LIST_ID = "listId"
        fun buildRoute(listId: Long) = "lists/$listId"
    }
    data object Categories : Routes("categories")
    data object CategoryProducts : Routes("categories/{categoryId}/products?name={categoryName}") {
        const val ARG_CATEGORY_ID = "categoryId"
        const val ARG_CATEGORY_NAME = "categoryName"
        fun buildRoute(categoryId: Long, categoryName: String) =
            "categories/$categoryId/products?name=${Uri.encode(categoryName)}"
    }
    data object Recurring : Routes("recurring")
    data object Settings : Routes("settings")
}
