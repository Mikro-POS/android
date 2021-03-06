package com.herlianzhang.mikropos.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.herlianzhang.mikropos.R
import com.herlianzhang.mikropos.ui.cart.CartScreen
import com.herlianzhang.mikropos.ui.cart.CartViewModel
import com.herlianzhang.mikropos.ui.common.AlertConfirmation
import com.herlianzhang.mikropos.ui.common.Screen
import com.herlianzhang.mikropos.ui.setting.MenuScreen
import com.herlianzhang.mikropos.ui.transaction.transaction_list.TransactionListScreen
import com.herlianzhang.mikropos.ui.transaction.transaction_list.TransactionListViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
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
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route
    var showDialog by remember { mutableStateOf(false) }

    rootNavController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<String>("qr-result").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                val data = newData ?: return@LaunchedEffect
                Timber.d("masuk ${data}")
                if (data.startsWith("transaction")) {
                    Timber.d("masuk ${data}")
                    val transactionId = data.split("/").getOrNull(1)?.toIntOrNull() ?: return@LaunchedEffect
                    rootNavController.navigate("transaction/${transactionId}")
                }
                savedState.remove<String>("qr-result")
            }
        }
    }

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
                HomeEvent.NavigateToQRScan -> {
                    rootNavController.navigate("qr_scanner")
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
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                HomeAction(
                    currentRoute,
                    viewModel,
                    onLogout = {
                        showDialog = true
                    }
                )
            }
        },
        bottomBar = {
            BottomNavigation {
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = if (isSelected) screen.iconFilled else screen.iconOutlined),
                                contentDescription = null
                            )
                        },
                        label = { Text(screen.label) },
                        selected = isSelected,
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
        Box {
            AnimatedNavHost(
                navController,
                startDestination = items.first().route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Cart.route) {
                    val viewModel = hiltViewModel<CartViewModel>()
                    CartScreen(rootNavController, viewModel)
                }
                composable(Screen.TransactionList.route) {
                    val viewModel = hiltViewModel<TransactionListViewModel>()
                    TransactionListScreen(rootNavController, viewModel)
                }
                composable(Screen.Menu.route) {
                    MenuScreen(rootNavController)
                }
            }
            AlertConfirmation(
                showDialog = showDialog,
                title = "Logout",
                message = "Apakah anda yakin ingin melakuan logout?",
                onConfirm = { viewModel.logout() },
                onDismiss = { showDialog = false }
            )
        }
    }
}