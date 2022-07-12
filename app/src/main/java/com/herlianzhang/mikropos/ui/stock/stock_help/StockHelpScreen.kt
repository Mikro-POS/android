package com.herlianzhang.mikropos.ui.stock.stock_help

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.StockHelpDialog
import com.herlianzhang.mikropos.vo.TransactionItem
import kotlinx.coroutines.launch

@Composable
fun StockHelpScreen(
    navController: NavController,
    transactionItems: List<TransactionItem>
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedItems: TransactionItem? by rememberSaveable {
        mutableStateOf(null)
    }
    var highlight: Int? by rememberSaveable {
        mutableStateOf(null)
    }
    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<String>("qr-result").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                val data = newData ?: return@LaunchedEffect
                if (data.startsWith("stock")) {
                    val params = data.split("/")
                    val stockId = params.getOrNull(1)?.toIntOrNull() ?: return@LaunchedEffect
                    if (selectedItems?.stocks?.any { stock -> stock.stockId == stockId} == true) {
                        highlight = stockId
                    } else {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Anda mengambil persediaan yang salah")
                        }
                    }
                } else {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("QR tidak valid")
                    }
                }
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
            StockHelpDialog(
                isShowDialog = isShowDialog,
                highlight = highlight,
                onDismiss = {
                    isShowDialog = false
                    selectedItems = null
                    highlight = null
                },
                onScan = { navController.navigate("qr_scanner") },
                item = selectedItems
            )
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}