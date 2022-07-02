package com.herlianzhang.mikropos.ui.transaction.transactionlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.EmptyView
import com.herlianzhang.mikropos.ui.common.ErrorView
import com.herlianzhang.mikropos.ui.common.LoadMoreView
import com.herlianzhang.mikropos.ui.common.LoadingView

@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionListViewModel
) {
    val transactions by viewModel.transactions.collectAsState()
    val isTransactionEmpty by viewModel.isTransactionEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(12.dp))
                }
                items(transactions) { item ->
                    TransactionItem(item) {
                        navController.navigate("transaction/${item.id}")
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
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        EmptyView(isTransactionEmpty)
        LoadingView(isLoading)
        ErrorView(isError, onClick = { viewModel.tryAgain() })
    }
}