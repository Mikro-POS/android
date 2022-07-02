package com.herlianzhang.mikropos.ui.stock.stockhelp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.vo.TransactionItem

@Composable
fun StockHelpScreen(
    navController: NavController,
    transactionItems: List<TransactionItem>
) {
    val scaffoldState = rememberScaffoldState()
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedItems: TransactionItem? by rememberSaveable {
        mutableStateOf(null)
    }
    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<String>("qr-result").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
//                dialogValue = newData ?: return@LaunchedEffect
                savedState.remove<String>("qr-result")
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
        topBar = {
            TopAppBar {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Ambil Persedian",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(48.dp))
            }
        }
    ) {
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        "Produk dalam transaksi",
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(transactionItems) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.clickable {
                            isShowDialog = true
                            selectedItems = item
                        }
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

                        Text(
                            item.product?.name ?: item.productName ?: "-",
                            modifier = Modifier.weight(1f)
                        )

                        Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                    }
                }
            }
            if (isShowDialog) {
                Dialog(
                    onDismissRequest = {
                        isShowDialog = false
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
                                        model = selectedItems?.product?.photo,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        selectedItems?.product?.name ?: selectedItems?.productName ?: "-",
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(onClick = { /*TODO*/ }) {
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

                            selectedItems?.stocks?.let { stocks ->
                                items(stocks) { item ->
                                    Column {
                                        Text(
                                            "#id: ${item.stockId}",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Ambil sebanyak: ${item.amount}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}