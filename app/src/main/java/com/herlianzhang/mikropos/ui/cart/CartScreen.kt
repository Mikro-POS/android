package com.herlianzhang.mikropos.ui.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.EmptyView

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel
) {
    val carts = viewModel.carts.collectAsState(emptyList())
    val isCartEmpty = viewModel.isCartEmpty.collectAsState(true)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(carts.value) { item ->
                    CartItem(
                        item,
                        onIncrease = { viewModel.increaseAmount(item) },
                        onDecrease = { viewModel.decreaseAmount(item) }
                    )
                }
            }
            AnimatedVisibility(
                !isCartEmpty.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = CircleShape,
                        onClick = { navController.navigate("checkout") }
                    ) {
                        Text("Pembayaran")
                    }
                    IconButton(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.primary),
                        onClick = { viewModel.clearCart() },
                    ) {
                        Icon(
                            Icons.Rounded.Clear,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        EmptyView(isCartEmpty.value)
    }
}