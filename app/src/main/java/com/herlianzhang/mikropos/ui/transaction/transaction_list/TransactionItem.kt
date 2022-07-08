package com.herlianzhang.mikropos.ui.transaction.transaction_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.Transaction


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionItem(
    item: Transaction,
    onClick: () -> Unit
) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.customer?.name ?: "-",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        item.createdAt?.formatDate("dd MMMM yyyy") ?: "-",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(item.status.color.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = item.status.value,
                    color = item.status.color,
                    style = MaterialTheme.typography.caption,
                )
            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = item.item?.product?.photo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.item?.product?.name ?: item.item?.productName ?: "-",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${item.item?.amount ?: 0} barang",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }
            if ((item.totalOtherItems ?: 0) > 0) {
                Text(
                    "+${item.totalOtherItems} produk lainnya",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column {
                Text(
                    "Total Belanja:",
                    style = MaterialTheme.typography.body2,
                )
                Text(
                    item.totalPrice.toRupiah(),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}