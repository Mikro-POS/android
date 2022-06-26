package com.herlianzhang.mikropos

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.herlianzhang.mikropos.ui.checkout.CheckoutScreen
import com.herlianzhang.mikropos.ui.checkout.CheckoutViewModel
import com.herlianzhang.mikropos.ui.customer.createcustomer.CreateCustomerScreen
import com.herlianzhang.mikropos.ui.customer.createcustomer.CreateCustomerViewModel
import com.herlianzhang.mikropos.ui.customer.customerdetail.CustomerDetailScreen
import com.herlianzhang.mikropos.ui.customer.customerdetail.CustomerDetailViewModel
import com.herlianzhang.mikropos.ui.customer.customerlist.CustomerListScreen
import com.herlianzhang.mikropos.ui.customer.customerlist.CustomerListViewModel
import com.herlianzhang.mikropos.ui.home.HomeScreen
import com.herlianzhang.mikropos.ui.home.HomeViewModel
import com.herlianzhang.mikropos.ui.login.LoginScreen
import com.herlianzhang.mikropos.ui.login.LoginViewModel
import com.herlianzhang.mikropos.ui.printer.PrinterListScreen
import com.herlianzhang.mikropos.ui.printer.PrinterListViewModel
import com.herlianzhang.mikropos.ui.product.createproduct.CreateProductScreen
import com.herlianzhang.mikropos.ui.product.createproduct.CreateProductViewModel
import com.herlianzhang.mikropos.ui.product.productdetail.ProductDetailScreen
import com.herlianzhang.mikropos.ui.product.productdetail.ProductDetailViewModel
import com.herlianzhang.mikropos.ui.product.productlist.ProductListScreen
import com.herlianzhang.mikropos.ui.product.productlist.ProductListViewModel
import com.herlianzhang.mikropos.ui.qrscan.QrScannerScreen
import com.herlianzhang.mikropos.ui.register.RegisterScreen
import com.herlianzhang.mikropos.ui.register.RegisterViewModel
import com.herlianzhang.mikropos.ui.stock.createstock.CreateStockScreen
import com.herlianzhang.mikropos.ui.stock.createstock.CreateStockViewModel
import com.herlianzhang.mikropos.ui.stock.stocklist.StockListScreen
import com.herlianzhang.mikropos.ui.stock.stocklist.StockListViewModel

@Composable
fun NavigationComponent(
    navController: NavHostController,
    isAuthenticated: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
//        startDestination = "checkout"
    ) {
        composable("login") {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                viewModel,
                navigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                navigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("register") {
            val viewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(
                viewModel,
                navigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                },
                navigateToHome = {
                    navController.navigate("home") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("home") {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navController,
                viewModel
            )
        }
        composable("product_list") {
            val viewModel = ProductListViewModel.getViewModel(isSelectMode = false)
            ProductListScreen(navController, viewModel)
        }
        composable("select_product") {
            val viewModel = ProductListViewModel.getViewModel(isSelectMode = true)
            ProductListScreen(navController, viewModel)
        }
        composable("create_product") {
            val viewModel = hiltViewModel<CreateProductViewModel>()
            CreateProductScreen(navController, viewModel)
        }
        composable(
            "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("productId")?.let { id ->
                val viewModel = ProductDetailViewModel.getViewModel(id)
                ProductDetailScreen(navController, viewModel)
            }
        }
        composable("customer_list") {
            val viewModel = CustomerListViewModel.getViewModel(isSelectMode = false)
            CustomerListScreen(navController, viewModel)
        }
        composable("select_customer") {
            val viewModel = CustomerListViewModel.getViewModel(isSelectMode = true)
            CustomerListScreen(navController, viewModel)
        }
        composable("create_customer") {
            val viewModel = hiltViewModel<CreateCustomerViewModel>()
            CreateCustomerScreen(navController, viewModel)
        }
        composable(
            "customer_detail/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("customerId")?.let { id ->
                val viewModel = CustomerDetailViewModel.getViewModel(id)
                CustomerDetailScreen(navController, viewModel)
            }
        }
        composable(
            "stock_list/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("productId")?.let { productId ->
                val viewModel = StockListViewModel.getViewModel(productId)
                StockListScreen(navController, viewModel)
            }
        }
        composable(
            "create_stock/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("productId")?.let { productId ->
                val viewModel = CreateStockViewModel.getViewModel(productId)
                CreateStockScreen(navController, viewModel)
            }
        }
        composable("qr_scanner") {
            QrScannerScreen(navController)
        }
        composable("printer_list") {
            val viewModel = hiltViewModel<PrinterListViewModel>()
            PrinterListScreen(viewModel)
        }
        composable("checkout") {
            val viewModel = hiltViewModel<CheckoutViewModel>()
            CheckoutScreen(navController, viewModel)
        }
    }
}