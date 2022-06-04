package com.herlianzhang.mikropos.ui.stock.stocklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Print
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.Stock
import com.herlianzhang.mikropos.vo.StockSource

@Composable
fun StockListScreen(
    productId: Int,
    navController: NavController,
    viewModel: StockListViewModel
) {
    val listState = rememberLazyListState()
    val stocks by viewModel.stocks.collectAsState()
    val isStockEmpty by viewModel.isProductEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Boolean>("refresh_stocks").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModel.refresh()
                savedState.remove<Boolean>("refresh_stocks")
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.setProductId(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Daftar Stok",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    navController.navigate("create_stock")
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                }
                items(stocks) { item ->
                    StockItem(
                        item,
                        onPrintClick = {
                            viewModel.printStock(item)
                        },
                        onDeleteClick = {

                        }
                    )
                }
                if (isLoadMore) {
                    item {
                        LoadMoreView(isLoadMore)
                        LaunchedEffect(true) {
                            viewModel.loadMore()
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
            EmptyView(isStockEmpty)
            LoadingView(isLoading)
            ErrorView(isError, onClick = { viewModel.tryAgain() })
        }
    }
}

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
                IconButton(onClick = { }) {
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
            DetailItem(key = "Total", value = stock.amount?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Terjual", value = stock.soldAmount?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Sisa", value = stock.amount?.minus(stock.soldAmount ?: 0)?.toString(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            DetailItem(key = "Harga", value = stock.purchasePrice.toRupiah(), paddingVertical = 12.dp, paddingHorizontal = 16.dp)
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray)
            )
            Text(
                stock.createdAt.formatDate(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}