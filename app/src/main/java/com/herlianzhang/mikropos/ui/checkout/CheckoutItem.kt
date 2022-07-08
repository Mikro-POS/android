package com.herlianzhang.mikropos.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.utils.extensions.toRupiah

@Composable
fun CheckoutItem(
    item: Cart
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.photo,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.name ?: "-",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.price.toRupiah(),
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.amount.toString(),
            modifier = Modifier.padding(horizontal = 6.dp),
            fontWeight = FontWeight.Bold
        )
    }
}