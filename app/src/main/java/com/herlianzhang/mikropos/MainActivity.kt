package com.herlianzhang.mikropos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.herlianzhang.mikropos.ui.customer.customerdetail.CustomerDetailViewModel
import com.herlianzhang.mikropos.ui.customer.customerlist.CustomerListViewModel
import com.herlianzhang.mikropos.ui.product.productdetail.ProductDetailViewModel
import com.herlianzhang.mikropos.ui.product.productlist.ProductListViewModel
import com.herlianzhang.mikropos.ui.stock.createstock.CreateStockViewModel
import com.herlianzhang.mikropos.ui.stock.stocklist.StockListViewModel
import com.herlianzhang.mikropos.ui.theme.MikroPOSTheme
import com.herlianzhang.mikropos.utils.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPref: UserPreferences

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun customerListViewModelFactory(): CustomerListViewModel.Factory
        fun productListViewModelFactory(): ProductListViewModel.Factory
        fun customerDetailViewModelFactory(): CustomerDetailViewModel.Factory
        fun productDetailViewModelFactory(): ProductDetailViewModel.Factory
        fun stockListViewModelFactory(): StockListViewModel.Factory
        fun createStockViewModelFactory(): CreateStockViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MikroPOSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationComponent(navController,  userPref.isAuthenticated)
                }
            }
        }
    }
}