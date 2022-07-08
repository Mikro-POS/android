package com.herlianzhang.mikropos.ui.expense.expense_category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.EmptyView
import com.herlianzhang.mikropos.ui.common.ErrorView
import com.herlianzhang.mikropos.ui.common.LoadMoreView
import com.herlianzhang.mikropos.ui.common.LoadingView

@Composable
fun ExpenseCategoryScreen(
    navController: NavController,
    viewModel: ExpenseCategoryViewModel
) {
    val listState = rememberLazyListState()
    val categories by viewModel.categories.collectAsState()
    val isCategoryEmpty by viewModel.isCategoryEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()
    
    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Boolean>("refresh_expense_categories").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModel.tryAgain()
                savedState.remove<Boolean>("refresh_expense_categories")
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Pilih Kategori",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    navController.navigate("create_expense_category")
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(categories) { item ->
                    ExpenseCategoryItem(item) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_category",
                            item
                        )
                        navController.popBackStack()
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

            EmptyView(isCategoryEmpty)
            LoadingView(isLoading)
            ErrorView(isError, onClick = { viewModel.tryAgain() })
        }
    }
}