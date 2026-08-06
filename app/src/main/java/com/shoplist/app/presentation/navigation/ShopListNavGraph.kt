package com.shoplist.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shoplist.app.R
import com.shoplist.app.presentation.categories.CategoriesScreen
import com.shoplist.app.presentation.categoryproducts.CategoryProductsScreen
import com.shoplist.app.presentation.listdetail.ListDetailScreen
import com.shoplist.app.presentation.recurring.RecurringTemplatesScreen
import com.shoplist.app.presentation.settings.SettingsScreen
import com.shoplist.app.presentation.shoplists.ShoppingListsScreen

private data class BottomTab(
    val route: String,
    @androidx.annotation.StringRes val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomTabs = listOf(
    BottomTab(Routes.ShoppingLists.route, R.string.tab_lists, Icons.AutoMirrored.Filled.ListAlt),
    BottomTab(Routes.Categories.route, R.string.tab_categories, Icons.Filled.Category),
    BottomTab(Routes.Recurring.route, R.string.tab_recurring, Icons.Filled.Repeat)
)

@Composable
fun ShopListNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isTopLevelDestination = bottomTabs.any { tab ->
                currentDestination?.hierarchy?.any { it.route == tab.route } == true
            }
            if (isTopLevelDestination) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.ShoppingLists.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.ShoppingLists.route) {
                ShoppingListsScreen(
                    onListClick = { list -> navController.navigate(Routes.ListDetail.buildRoute(list.id)) },
                    onSettingsClick = { navController.navigate(Routes.Settings.route) }
                )
            }
            composable(
                route = Routes.ListDetail.route,
                arguments = listOf(navArgument(Routes.ListDetail.ARG_LIST_ID) { type = NavType.LongType })
            ) {
                ListDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.Categories.route) {
                CategoriesScreen(
                    onCategoryClick = { category ->
                        navController.navigate(Routes.CategoryProducts.buildRoute(category.id, category.name))
                    }
                )
            }
            composable(
                route = Routes.CategoryProducts.route,
                arguments = listOf(
                    navArgument(Routes.CategoryProducts.ARG_CATEGORY_ID) { type = NavType.LongType },
                    navArgument(Routes.CategoryProducts.ARG_CATEGORY_NAME) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString(Routes.CategoryProducts.ARG_CATEGORY_NAME).orEmpty()
                CategoryProductsScreen(
                    categoryName = categoryName,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Recurring.route) {
                RecurringTemplatesScreen()
            }
            composable(Routes.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
