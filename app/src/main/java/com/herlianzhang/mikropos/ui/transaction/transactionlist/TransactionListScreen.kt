package com.herlianzhang.mikropos.ui.transaction.transactionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.herlianzhang.mikropos.ui.common.*

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
    val filterWording by viewModel.filterWording.collectAsState()
    val isBillingPeriod by viewModel.isBillingPeriod.collectAsState()
    val isNotYetPaidOff by viewModel.isNotYetPaidOff.collectAsState()
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(
                                1.dp,
                                if (filterWording != null) MaterialTheme.colors.primary else Color.Gray,
                                RoundedCornerShape(50)
                            )
                            .clickable { isShowDialog = true }
                            .background(
                                if (filterWording != null) MaterialTheme.colors.primary.copy(
                                    0.2f
                                ) else Color.Transparent
                            )
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.FilterList,
                            tint = if (filterWording != null) MaterialTheme.colors.primary else Color.Black,
                            contentDescription = null
                        )
                        Text(
                            filterWording ?: "Saring",
                            color = if (filterWording != null) MaterialTheme.colors.primary else Color.Black
                        )
                    }
                }

                item {
                    Text(
                        "Masa Penagihan",
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(
                                1.dp,
                                if (isBillingPeriod) MaterialTheme.colors.primary else Color.Gray,
                                RoundedCornerShape(50)
                            )
                            .background(
                                if (isBillingPeriod) MaterialTheme.colors.primary.copy(
                                    0.2f
                                ) else Color.Transparent
                            )
                            .clickable { viewModel.filterBillingPeriod() }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        color = if (isBillingPeriod) MaterialTheme.colors.primary else Color.Black
                    )
                }

                item {
                    Text(
                        "Belum Lunas",
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(
                                1.dp,
                                if (isNotYetPaidOff) MaterialTheme.colors.primary else Color.Gray,
                                RoundedCornerShape(50)
                            )
                            .background(
                                if (isNotYetPaidOff) MaterialTheme.colors.primary.copy(
                                    0.2f
                                ) else Color.Transparent
                            )
                            .clickable { viewModel.filterNotYetPaidOff() }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        color = if (isNotYetPaidOff) MaterialTheme.colors.primary else Color.Black
                    )
                }
            }
            SwipeRefresh(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = rememberSwipeRefreshState(isLoading),
                onRefresh = { viewModel.refresh() }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
        }
        EmptyView(isTransactionEmpty)
        LoadingView(isLoading)
        ErrorView(isError, onClick = { viewModel.tryAgain() })
        TransactionFilterDialog(
            isDismiss = isShowDialog,
            onDismiss = { isShowDialog = false },
            onSubmit = { startDate, endDate ->
                isShowDialog = false
                viewModel.filterDate(startDate, endDate)
            }
        )
    }
}