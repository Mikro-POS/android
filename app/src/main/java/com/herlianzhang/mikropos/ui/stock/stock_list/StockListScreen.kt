package com.herlianzhang.mikropos.ui.stock.stock_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StockListScreen(
    navController: NavController,
    viewModel: StockListViewModel
) {
    val listState = rememberLazyListState()
    val stocks by viewModel.stocks.collectAsState()
    val isStockEmpty by viewModel.isProductEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()
    var showAlertConfirmation by remember { mutableStateOf(Pair(false, -1)) }
    val scaffoldState = rememberScaffoldState()

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Boolean>("refresh_stocks").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModel.refresh()
                savedState.remove<Boolean>("refresh_stocks")
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "refresh_product_detail",
                    true
                )
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is StockListEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is StockListEvent.SendResult -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_product_detail",
                        true
                    )
                }
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
                    "Daftar Stok",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    navController.navigate("create_stock/${viewModel.productId}")
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
                    .fillMaxSize()
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
                            showAlertConfirmation = Pair(true, item.id)
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
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
            AlertConfirmation(
                showDialog = showAlertConfirmation.first,
                title = "Hapus",
                message = "Apakah anda yakin ingin menghapus Persediaan ini?",
                onConfirm = { viewModel.deleteStock(showAlertConfirmation.second) },
                onDismiss = { showAlertConfirmation = Pair(false, -1) }
            )
        }
    }
}