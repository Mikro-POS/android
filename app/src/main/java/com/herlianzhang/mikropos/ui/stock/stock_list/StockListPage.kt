package com.herlianzhang.mikropos.ui.stock.stock_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Print
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.R
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.vo.Stock
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun StockListPage(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: StockListViewModelInterface,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()
    val stocks by viewModel.stocks.collectAsState()
    val isStockEmpty by viewModel.isProductEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()
    var showAlertConfirmation by remember { mutableStateOf(Pair(false, -1)) }
    var isShowDialog by remember {
        mutableStateOf(false)
    }
    var selectedStock: Stock? by remember {
        mutableStateOf(null)
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
                is StockListEvent.Refresh -> {
                    isShowDialog = false
                    onRefresh()
                }
            }
        }
    }

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
                    onDeleteClick = {
                        showAlertConfirmation = Pair(true, item.id)
                    }
                ) {
                    IconButton(onClick = {
                        if (viewModel is StockListViewModel) {
                            viewModel.printStock(item)
                        } else {
                            selectedStock = item
                            isShowDialog = true
                        }
                    }) {
                        if (viewModel is StockListViewModel) {
                            Icon(Icons.Rounded.Print, contentDescription = null, tint = MaterialTheme.colors.primary)
                        } else {
                            Icon(painter = painterResource(R.drawable.ic_refund), contentDescription = null, tint = MaterialTheme.colors.primary, modifier = Modifier.size(24.dp))
                        }

                    }
                }
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
        if (viewModel is StockListExpiredViewModel && selectedStock != null) {
            val isDialogLoading by viewModel.isDialogLoading.collectAsState()

            RefundDialog(
                stock = selectedStock,
                isLoading = isDialogLoading,
                isDismiss = isShowDialog,
                onDismiss = {
                    viewModel.cancelRefund()
                    selectedStock = null
                    isShowDialog = false
                },
                onSubmit = { productId, stockId, refund ->
                    viewModel.refundStock(productId, stockId, refund)
                }
            )
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