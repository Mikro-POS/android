package com.herlianzhang.mikropos.ui.stock.stock_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Print
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.herlianzhang.mikropos.ui.common.DetailItem
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.Stock
import com.herlianzhang.mikropos.vo.StockSource

@Composable
fun StockItem(
    stock: Stock,
    onPrintClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stock.sourceString,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(bottomEnd = 16.dp))
                        .background(if (stock.source == StockSource.SUPPLIER) MaterialTheme.colors.primary else MaterialTheme.colors.secondary)
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onPrintClick?.invoke() }) {
                    Icon(Icons.Rounded.Print, contentDescription = null)
                }
                IconButton(onClick = { onDeleteClick?.invoke() }) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (stock.source == StockSource.SUPPLIER) {
                DetailItem(key = "Nama Pemasok", value = stock.supplierName ?: "-", paddingVertical = 12.dp, paddingHorizontal = 16.dp)
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .padding(horizontal = 16.dp)
                        .background(Color.LightGray)
                )
            }
            DetailItem(key = "ID", value = "#${stock.id}", paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Jumlah Total", value = stock.amount?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Jumlah Terjual", value = stock.soldAmount?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Sisa Persediaan", value = stock.amount?.minus(stock.soldAmount ?: 0)?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Harga Beli", value = stock.purchasePrice.toRupiah(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray)
            )
            Text(
                stock.createdAt.formatDate() ?: "-",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}