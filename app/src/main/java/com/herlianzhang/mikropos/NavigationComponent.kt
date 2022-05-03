package com.herlianzhang.mikropos

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.herlianzhang.mikropos.ui.common.QrScannerScreen
import com.herlianzhang.mikropos.ui.createproduct.CreateProduct
import com.herlianzhang.mikropos.ui.createproduct.CreateProductViewModel
import com.herlianzhang.mikropos.ui.home.HomeScreen
import com.herlianzhang.mikropos.ui.home.HomeViewModel
import com.herlianzhang.mikropos.ui.login.LoginScreen
import com.herlianzhang.mikropos.ui.login.LoginViewModel
import com.herlianzhang.mikropos.ui.productdetail.ProductDetailScreen
import com.herlianzhang.mikropos.ui.productlist.ProductListScreen
import com.herlianzhang.mikropos.ui.productlist.ProductListViewModel
import com.herlianzhang.mikropos.ui.register.RegisterScreen
import com.herlianzhang.mikropos.ui.register.RegisterViewModel

@Composable
fun NavigationComponent(
    navController: NavHostController,
    isAuthenticated: Boolean
) {
    NavHost(
        navController = navController,
//        startDestination = "product_list"
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
            CreateProduct(navController, viewModel)
        }
        composable(
            "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("productId")?.let { id ->
                ProductDetailScreen(id)
            }
        }
        composable("qr_scanner") {
            QrScannerScreen(navController)
        }
    }
}