package com.herlianzhang.mikropos.ui.transaction.transactiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.TransactionItem

@Composable
fun TransactionDetailItem(item: TransactionItem) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = item.product?.photo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(
                        item.product?.name ?: item.productName ?: "-",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${item.amount} x ${item.price?.div(item.amount ?: 0).toRupiah()}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray)
            )

            Text(
                "Total Harga",
                style = MaterialTheme.typography.body1,
            )
            Text(
                item.price.toRupiah(),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
        }
    }
}