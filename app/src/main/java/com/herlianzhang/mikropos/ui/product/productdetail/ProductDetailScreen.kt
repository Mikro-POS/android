package com.herlianzhang.mikropos.ui.product.productdetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.utils.toRupiah
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProductDetailScreen(
    id: Int,
    navController: NavController,
    viewModel: ProductDetailViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val isNotFound by viewModel.isNotFound.collectAsState()
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var dialogKey by rememberSaveable {
        mutableStateOf("")
    }
    var dialogValue by rememberSaveable {
        mutableStateOf("")
    }
    var dialogTitle by rememberSaveable {
        mutableStateOf("")
    }
    var dialogType: EditDialogType by rememberSaveable {
        mutableStateOf(EditDialogType.Default)
    }
    val isDialogLoading by viewModel.isDialogLoading.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@rememberLauncherForActivityResult
            viewModel.uploadImage(uri)
        }

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<String>("qr-result").let {
            val newData by it.observeAsState()
            LaunchedEffect(newData) {
                dialogValue = newData ?: return@LaunchedEffect
                savedState.remove<String>("qr-result")
            }
        }
    }

    LaunchedEffect(key1 = true) {
        if (data == null)
            viewModel.setProductId(id)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProductDetailEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is ProductDetailEvent.HideDialog -> {
                    isShowDialog = false
                }
                is ProductDetailEvent.SetHasChanges -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_products",
                        true
                    )
                }
                is ProductDetailEvent.Back -> {
                    navController.popBackStack()
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
                    "Detail Produk",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = { viewModel.deleteProduct() },
                    enabled = !isLoading && data != null
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                }
            }
        }
    ) {
        Box {
            data?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .clip(CircleShape)
                                .fillMaxSize()
                                .background(Color.LightGray),
                            model = data.photo,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                        IconButton(
                            enabled = !isUploadingImage ,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colors.onBackground.copy(alpha = 0.7f)),
                            onClick = {
                                launcher.launch("image/*")
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Edit,
                                tint = MaterialTheme.colors.background.copy(alpha = 0.7f),
                                contentDescription = null
                            )
                        }
                        UploadImageLoadingView(
                            isUploadingImage,
                            modifier = Modifier.clip(CircleShape)
                        )
                    }

                    DetailItem(key = "Nama", value = data.name) {
                        dialogKey = "name"
                        dialogValue = data.name ?: ""
                        dialogTitle = "Ubah Nama"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
                    }
                    DetailItem(key = "Harga", value = data.price?.toRupiah()) {
                        dialogKey = "price"
                        dialogValue = data.price?.toString() ?: ""
                        dialogTitle = "Ubah Harga"
                        dialogType = EditDialogType.Currency
                        isShowDialog = true
                    }
                    DetailItem(key = "SKU", value = data.sku) {
                        dialogKey = "sku"
                        dialogValue = data.sku ?: ""
                        dialogTitle = "Ubah SKU"
                        dialogType = EditDialogType.QrCode
                        isShowDialog = true
                    }
                    DetailItem(key = "Total Stok", value = data.totalStock?.toString()) {
                        navController.navigate("stock_list/$id")
                    }
                }
            }
            LoadingView(isLoading)
            ErrorView(isError, onClick = { viewModel.tryAgain() })
            EditDialog(
                value = dialogValue,
                title = dialogTitle,
                changeValue = { dialogValue = it },
                isLoading = isDialogLoading,
                type = dialogType,
                isDismiss = isShowDialog,
                onDismiss = {
                    viewModel.cancelUpdateProduct()
                    isShowDialog = false
                },
                onSubmit = {
                    val params = mutableMapOf<String, Any>()
                    if (dialogType == EditDialogType.Currency) {
                        val value = dialogValue.toLongOrNull() ?: return@EditDialog
                        params[dialogKey] = value
                    } else {
                        params[dialogKey] = dialogValue
                    }
                    viewModel.updateProduct(params)
                },
                navigateToScanner = { navController.navigate("qr_scanner") }
            )
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
            NotFoundView(isNotFound)
        }
    }
}
