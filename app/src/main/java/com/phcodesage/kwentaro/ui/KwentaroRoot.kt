package com.phcodesage.kwentaro.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phcodesage.kwentaro.ui.dashboard.DashboardScreen
import com.phcodesage.kwentaro.ui.products.ProductEditorScreen
import com.phcodesage.kwentaro.ui.products.ProductsScreen
import com.phcodesage.kwentaro.ui.register.RegisterScreen
import com.phcodesage.kwentaro.ui.sales.ReceiptScreen
import com.phcodesage.kwentaro.ui.sales.SalesScreen
import com.phcodesage.kwentaro.ui.settings.SettingsScreen

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
        if (wide) {
            Row {
                if (showNav) {
                    NavigationRail(header = {
                        Text("K", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 12.dp))
                    }) {
                        Spacer(Modifier.height(8.dp))
                        Tab.entries.forEach { tab ->
                            NavigationRailItem(
                                selected = tab == currentTab,
                                onClick = { go(tab) },
                                icon = { Icon(if (tab == currentTab) tab.selectedIcon else tab.icon, null) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
                KwentaroNavHost(nav, wide = true)
            }
        } else {
            Scaffold(
                bottomBar = {
                    if (showNav) NavigationBar {
                        Tab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = tab == currentTab,
                                onClick = { go(tab) },
                                icon = { Icon(if (tab == currentTab) tab.selectedIcon else tab.icon, null) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                },
                contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
            ) { padding ->
                KwentaroNavHost(nav, wide = false, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun KwentaroNavHost(nav: NavHostController, wide: Boolean, modifier: Modifier = Modifier) {
    NavHost(nav, startDestination = Tab.Register.route, modifier = modifier) {
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
