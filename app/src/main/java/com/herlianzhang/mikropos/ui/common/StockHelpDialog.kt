package com.herlianzhang.mikropos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.vo.TransactionItem

@Composable
fun StockHelpDialog(
    item: TransactionItem?,
    highlight: Int?,
    isShowDialog: Boolean,
    onDismiss: () -> Unit,
    onScan: () -> Unit
) {
    if (isShowDialog) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            }
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = item?.product?.photo,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                item?.product?.name ?: item?.productName ?: "-",
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = { onScan() }) {
                                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50))
                                .background(Color.LightGray)
                        )
                    }

                    item?.stocks?.let { stocks ->
                        items(stocks) { item ->
                            val color = if (highlight == item.stockId) MaterialTheme.colors.primary else Color.Black
                            Column {
                                Text(
                                    "#id: ${item.stockId}",
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                                Text(
                                    "Ambil sebanyak: ${item.amount}",
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}