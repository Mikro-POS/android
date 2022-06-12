package com.herlianzhang.mikropos.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.herlianzhang.mikropos.ui.common.Screen
import com.herlianzhang.mikropos.ui.setting.MenuScreen
import com.herlianzhang.mikropos.ui.transaction.cart.CartScreen
import com.herlianzhang.mikropos.ui.transaction.transactionlist.TransactionListScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    rootNavController: NavController,
    viewModel: HomeViewModel
) {
    val items = listOf(
        Screen.Cart,
        Screen.TransactionList,
        Screen.Menu
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                HomeEvent.Logout -> {
                    rootNavController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
                HomeEvent.NavigateToSelectProduct -> {
                    rootNavController.navigate("select_product")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar {
                Spacer(Modifier.width(48.dp))
                Text(
                    Screen.getLabel(currentRoute),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                HomeAction(currentRoute, viewModel)
            }
        },
        bottomBar = {
            BottomNavigation {
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = items.first().route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Cart.route) {
                CartScreen()
            }
            composable(Screen.TransactionList.route) {
                TransactionListScreen()
            }
            composable(Screen.Menu.route) {
                MenuScreen(rootNavController)
            }
        }
    }
}