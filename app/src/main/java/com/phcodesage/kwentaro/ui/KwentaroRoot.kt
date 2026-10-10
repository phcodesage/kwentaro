package com.phcodesage.kwentaro.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phcodesage.kwentaro.R
import com.phcodesage.kwentaro.ui.dashboard.DashboardScreen
import com.phcodesage.kwentaro.ui.products.ProductEditorScreen
import com.phcodesage.kwentaro.ui.products.ProductsScreen
import com.phcodesage.kwentaro.ui.register.RegisterScreen
import com.phcodesage.kwentaro.ui.sales.ReceiptScreen
import com.phcodesage.kwentaro.ui.sales.SalesScreen
import com.phcodesage.kwentaro.ui.settings.SettingsScreen
import com.phcodesage.kwentaro.ui.theme.SolidSystemBars

private enum class Tab(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    Register("register", "Sell", Icons.Outlined.PointOfSale, Icons.Rounded.PointOfSale),
    Products("products", "Products", Icons.Outlined.Inventory2, Icons.Rounded.Inventory2),
    Sales("sales", "Sales", Icons.Outlined.ReceiptLong, Icons.Rounded.ReceiptLong),
    Dashboard("dashboard", "Insights", Icons.Outlined.Insights, Icons.Rounded.Insights),
    Settings("settings", "Settings", Icons.Outlined.Settings, Icons.Rounded.Settings),
}

@Composable
fun KwentaroRoot() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val currentTab = Tab.entries.firstOrNull { it.route == route }
    val showNav = currentTab != null

    fun go(tab: Tab) = nav.navigate(tab.route) {
        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    BoxWithConstraints {
        val wide = maxWidth >= 600.dp
        val colors = MaterialTheme.colorScheme
        SolidSystemBars(colors.primary, if (showNav && !wide) colors.primary else colors.surface)
        if (wide) {
            Row(Modifier.fillMaxSize().background(colors.primary)) {
                if (showNav) {
                    NavigationRail(containerColor = colors.primary, contentColor = colors.onPrimary, header = {
                        Surface(
                            color = colorResource(R.color.ic_launcher_background),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(vertical = 12.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_kwentaro_mark),
                                contentDescription = "Kwentaro",
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }) {
                        Spacer(Modifier.height(8.dp))
                        Tab.entries.forEach { tab ->
                            NavigationRailItem(
                                selected = tab == currentTab,
                                onClick = { go(tab) },
                                icon = { Icon(if (tab == currentTab) tab.selectedIcon else tab.icon, null) },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = colors.onTertiaryContainer,
                                    selectedTextColor = colors.onPrimary,
                                    indicatorColor = colors.tertiaryContainer,
                                    unselectedIconColor = colors.onPrimary,
                                    unselectedTextColor = colors.onPrimary,
                                ),
                            )
                        }
                    }
                }
                KwentaroNavHost(nav, wide = true, modifier = Modifier.weight(1f).background(colors.surface))
            }
        } else {
            Scaffold(
                bottomBar = {
                    if (showNav) NavigationBar(containerColor = colors.primary, contentColor = colors.onPrimary, tonalElevation = 0.dp) {
                        Tab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = tab == currentTab,
                                onClick = { go(tab) },
                                icon = { Icon(if (tab == currentTab) tab.selectedIcon else tab.icon, null) },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colors.onTertiaryContainer,
                                    selectedTextColor = colors.onPrimary,
                                    indicatorColor = colors.tertiaryContainer,
                                    unselectedIconColor = colors.onPrimary,
                                    unselectedTextColor = colors.onPrimary,
                                ),
                            )
                        }
                    }
                },
                contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
            ) { padding ->
                KwentaroNavHost(nav, wide = false, modifier = Modifier.padding(padding).consumeWindowInsets(padding))
            }
        }
    }
}

@Composable
private fun KwentaroNavHost(nav: NavHostController, wide: Boolean, modifier: Modifier = Modifier) {
    NavHost(nav, startDestination = Tab.Register.route, modifier = modifier, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }) {
        composable(Tab.Register.route) {
            RegisterScreen(wide = wide, onCheckedOut = { id -> nav.navigate("receipt/$id") })
        }
        composable(Tab.Products.route) {
            ProductsScreen(onEdit = { id -> nav.navigate("product/$id") }, onAdd = { nav.navigate("product/0") })
        }
        composable("product/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
            ProductEditorScreen(productId = it.arguments?.getLong("id") ?: 0L, onDone = { nav.popBackStack() })
        }
        composable(Tab.Sales.route) {
            SalesScreen(onOpen = { id -> nav.navigate("receipt/$id") })
        }
        composable("receipt/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
            ReceiptScreen(saleId = it.arguments?.getLong("id") ?: 0L, onBack = { nav.popBackStack() })
        }
        composable(Tab.Dashboard.route) { DashboardScreen() }
        composable(Tab.Settings.route) { SettingsScreen() }
    }
}
