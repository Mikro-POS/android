package com.herlianzhang.mikropos.ui.productlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.ui.common.EmptyView
import com.herlianzhang.mikropos.ui.common.ErrorView
import com.herlianzhang.mikropos.ui.common.LoadMoreView
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.utils.toRupiah
import com.herlianzhang.mikropos.vo.Product

@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClicked()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = product.photo,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                product.name,
                style = MaterialTheme
                    .typography
                    .h6
                    .copy(fontWeight = FontWeight.Bold)
            )
            Text(
                product.price.toRupiah(),
                style = MaterialTheme
                    .typography
                    .body2
                    .copy(color = Color.Gray)
            )
        }
    }
}

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
        savedState.getLiveData<Product>("refresh_products").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                newData ?: return@LaunchedEffect
                viewModel.search(search, false)
                savedState.remove<Product>("refresh_products")
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
                    "Produk",
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
                        ProductItem(
                            item,
                            Modifier.animateItemPlacement(),
                            onClicked = {
                                navController.navigate("product_detail/${item.id}")
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