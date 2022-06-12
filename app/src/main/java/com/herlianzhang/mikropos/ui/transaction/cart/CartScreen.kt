package com.herlianzhang.mikropos.ui.transaction.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.ui.common.EmptyView
import com.herlianzhang.mikropos.utils.extensions.toRupiah

@Composable
fun CartScreen(viewModel: CartViewModel) {
    val carts = viewModel.carts.collectAsState(emptyList())
    val isCartEmpty = viewModel.isCartEmpty.collectAsState(false)

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(carts.value) { item ->
                    CartItem(
                        item,
                        onIncrease = { viewModel.increaseAmount(item) },
                        onDecrease = { viewModel.decreaseAmount(item) }
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { }
            ) {
                Text("Bayar")
            }
        }
        EmptyView(isCartEmpty.value)
    }
}