package com.herlianzhang.mikropos.ui.stock.stock_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StockListScreen(
    navController: NavController,
    viewModels: List<StockListViewModelInterface>,
    productJSON: String
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState()

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Boolean>("refresh_stocks").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModels.forEach { viewModel ->
                    viewModel.refresh()
                }
                savedState.remove<Boolean>("refresh_stocks")
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "refresh_product_detail",
                    true
                )
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
                    "Daftar Persediaan",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    navController.navigate("create_stock/$productJSON")
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) {
        Box {
            Column {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    }
                ) {
                    viewModels.forEachIndexed { index, viewModel ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(if (viewModel is StockListExpiredViewModel) "Kedaluwarsa" else "Aktif") }
                        )
                    }
                }
                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    count = viewModels.size,
                    state = pagerState
                ) { index ->
                    StockListPage(navController, scaffoldState, viewModels[index])
                }
            }
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}