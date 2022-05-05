package com.herlianzhang.mikropos

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.herlianzhang.mikropos.ui.createcustomer.CreateCustomerScreen
import com.herlianzhang.mikropos.ui.createcustomer.CreateCustomerViewModel
import com.herlianzhang.mikropos.ui.qrscan.QrScannerScreen
import com.herlianzhang.mikropos.ui.createproduct.CreateProductScreen
import com.herlianzhang.mikropos.ui.createproduct.CreateProductViewModel
import com.herlianzhang.mikropos.ui.createsupplier.CreateSupplierScreen
import com.herlianzhang.mikropos.ui.createsupplier.CreateSupplierViewModel
import com.herlianzhang.mikropos.ui.customerdetail.CustomerDetailScreen
import com.herlianzhang.mikropos.ui.customerdetail.CustomerDetailViewModel
import com.herlianzhang.mikropos.ui.customerlist.CustomerListScreen
import com.herlianzhang.mikropos.ui.customerlist.CustomerListViewModel
import com.herlianzhang.mikropos.ui.home.HomeScreen
import com.herlianzhang.mikropos.ui.home.HomeViewModel
import com.herlianzhang.mikropos.ui.login.LoginScreen
import com.herlianzhang.mikropos.ui.login.LoginViewModel
import com.herlianzhang.mikropos.ui.printer.PrinterListScreen
import com.herlianzhang.mikropos.ui.printer.PrinterListViewModel
import com.herlianzhang.mikropos.ui.productdetail.ProductDetailScreen
import com.herlianzhang.mikropos.ui.productdetail.ProductDetailViewModel
import com.herlianzhang.mikropos.ui.productlist.ProductListScreen
import com.herlianzhang.mikropos.ui.productlist.ProductListViewModel
import com.herlianzhang.mikropos.ui.register.RegisterScreen
import com.herlianzhang.mikropos.ui.register.RegisterViewModel
import com.herlianzhang.mikropos.ui.supplierdetail.SupplierDetailScreen
import com.herlianzhang.mikropos.ui.supplierdetail.SupplierDetailViewModel
import com.herlianzhang.mikropos.ui.supplierlist.SupplierListScreen
import com.herlianzhang.mikropos.ui.supplierlist.SupplierListViewModel

@Composable
fun NavigationComponent(
    navController: NavHostController,
    isAuthenticated: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
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
                viewModel,
                navigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("product_list") {
            val viewModel = hiltViewModel<ProductListViewModel>()
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
                val viewModel = hiltViewModel<ProductDetailViewModel>()
                ProductDetailScreen(id, navController, viewModel)
            }
        }
        composable("supplier_list") {
            val viewModel = hiltViewModel<SupplierListViewModel>()
            SupplierListScreen(navController, viewModel)
        }
        composable("create_supplier") {
            val viewModel = hiltViewModel<CreateSupplierViewModel>()
            CreateSupplierScreen(navController, viewModel)
        }
        composable(
            "supplier_detail/{supplierId}",
            arguments = listOf(navArgument("supplierId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("supplierId")?.let { id ->
                val viewModel = hiltViewModel<SupplierDetailViewModel>()
                SupplierDetailScreen(id, navController, viewModel)
            }
        }
        composable("customer_list") {
            val viewModel = hiltViewModel<CustomerListViewModel>()
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
                val viewModel = hiltViewModel<CustomerDetailViewModel>()
                CustomerDetailScreen(id, navController, viewModel)
            }
        }
        composable("qr_scanner") {
            QrScannerScreen(navController)
        }
        composable("printer_list") {
            val viewModel = hiltViewModel<PrinterListViewModel>()
            PrinterListScreen(viewModel)
        }
    }
}