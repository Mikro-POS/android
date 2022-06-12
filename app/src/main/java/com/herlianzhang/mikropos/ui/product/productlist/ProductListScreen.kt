package com.herlianzhang.mikropos.ui.product.productlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.ui.product.productdetail.ProductDetailEvent
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: ProductListViewModel
) {
    var search by rememberSaveable {
        mutableStateOf("")
    }
    val listState = rememberLazyListState()
    val title by viewModel.title.collectAsState()
    val products by viewModel.products.collectAsState()
    val isProductEmpty by viewModel.isProductEmpty.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadMore by viewModel.isLoadMore.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val localFocusManager = LocalFocusManager.current

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<String>("qr-result").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                val data = newData ?: return@LaunchedEffect
                search = data
                viewModel.search(data, false)
                savedState.remove<String>("qr-result")
            }
        }
    }

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Boolean>("refresh_products").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModel.search(search, false)
                savedState.remove<Boolean>("refresh_products")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProductListEvent.NavigateToProductDetail -> {
                    navController.navigate("product_detail/${event.id}")
                }
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
                    title,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    navController.navigate("create_product")
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        value = search,
                        label = {
                            Text("Cari")
                        },
                        onValueChange = {
                            search = it
                            viewModel.search(it)
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                localFocusManager.clearFocus()
                            }
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = { navController.navigate("qr_scanner") }) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                    }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(products) { item ->
                        ListItem(
                            item.photo,
                            item.name,
                            item.price?.toRupiah(),
                            Modifier.animateItemPlacement(),
                            "stok ${item.totalStock}",
                            onClicked = {
                                viewModel.onClickProduct(item)
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
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            EmptyView(isProductEmpty)
            LoadingView(isLoading)
            ErrorView(isError, onClick = { viewModel.tryAgain() })
        }
    }
}