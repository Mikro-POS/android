package com.herlianzhang.mikropos.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.herlianzhang.mikropos.ui.createtransaction.CreateTransactionScreen
import com.herlianzhang.mikropos.ui.productlist.ProductListScreen
import com.herlianzhang.mikropos.ui.setting.SettingScreen
import com.herlianzhang.mikropos.ui.transactionlist.TransactionListScreen
import kotlinx.coroutines.flow.collectLatest

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object CreateTransaction : Screen("createTransaction", "Buat", Icons.Rounded.Add)
    object TransactionList : Screen("transactionList", "Transaksi", Icons.Rounded.List)
    object Setting : Screen("setting", "Pengaturan", Icons.Rounded.Settings)
}

fun getLabel(route: String?): String {
    return when (route) {
        Screen.CreateTransaction.route -> Screen.CreateTransaction.label
        Screen.TransactionList.route -> Screen.TransactionList.label
        Screen.Setting.route -> Screen.Setting.label
        else -> ""
    }
}

@Composable
fun HomeAction(route: String?, viewModel: HomeViewModel) {
    when (route) {
        Screen.CreateTransaction.route -> {
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
        Screen.TransactionList.route -> {
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
            }
        }
        Screen.Setting.route -> {
            IconButton(onClick = {
                viewModel.logout()
            }) {
                Icon(Icons.Rounded.Logout, contentDescription = null)
            }
        }
    }
}

@Composable
fun HomeScreen(rootNavController: NavController, viewModel: HomeViewModel, navigateToLogin: () -> Unit) {
    val items = listOf(
        Screen.CreateTransaction,
        Screen.TransactionList,
        Screen.Setting
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest { event ->
            when(event) {
                HomeEvent.Logout -> navigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar {
                Spacer(Modifier.width(48.dp))
                Text(
                    getLabel(currentRoute) ,
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
        NavHost(navController, startDestination = items.first().route, Modifier.padding(innerPadding)) {
            composable(Screen.CreateTransaction.route) {
                CreateTransactionScreen()
            }
            composable(Screen.TransactionList.route) {
                TransactionListScreen()
            }
            composable(Screen.Setting.route) {
                SettingScreen(rootNavController)
            }
        }
    }
}