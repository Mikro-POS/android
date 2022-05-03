package com.herlianzhang.mikropos.ui.createproduct

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.herlianzhang.mikropos.vo.Product

@Composable
fun CreateProduct(navController: NavController) {
    Button(onClick = {
        navController.previousBackStackEntry?.savedStateHandle?.let {
            val product = Product(
                id = 200,
                name = "barang baru",
                price = 300000,
                photo = null
            )
            it.set("new_data", product)
        }
        navController.popBackStack()
    }) {
        Text("go result")
    }
}