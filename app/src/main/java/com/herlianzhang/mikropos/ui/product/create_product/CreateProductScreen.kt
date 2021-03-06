package com.herlianzhang.mikropos.ui.product.create_product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.ui.common.UploadImageLoadingView
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateProductScreen(
    navController: NavController,
    viewModel: CreateProductViewModel
) {
    val localFocusManager = LocalFocusManager.current
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var sku by rememberSaveable {
        mutableStateOf("")
    }
    val bitmap by viewModel.bitmap.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val isLoading by viewModel.isLoading.collectAsState()
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
                val data = newData ?: return@LaunchedEffect
                sku = data
                savedState.remove<String>("qr-result")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CreateProductEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CreateProductEvent.NavigateToDetail -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_products",
                        true
                    )
                    if (event.id == null)
                        navController.popBackStack()
                    else
                        navController.navigate("product_detail/${event.id}") {
                            popUpTo("create_product") {
                                inclusive = true
                            }
                        }
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
                    "Tambah Produk",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        viewModel.createProduct(
                            name,
                            sku
                        )
                    },
                    enabled = name.isNotBlank()
                ) {
                    Icon(Icons.Rounded.Done, contentDescription = null)
                }
            }
        }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(150.dp)
                        .background(Color.LightGray)
                        .clickable {
                            launcher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.AddPhotoAlternate,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray,
                        contentDescription = null
                    )
                    bitmap?.let {
                        Image(
                            it.asImageBitmap(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    UploadImageLoadingView(isUploadingImage)
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Nama Produk")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                    ),
                    onValueChange = { name = it }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = sku,
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text("Stock Keeping Unit (SKU)")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                localFocusManager.clearFocus()
                                if (name.isNotBlank())
                                    viewModel.createProduct(
                                        name,
                                        sku
                                    )
                            }
                        ),
                        onValueChange = { sku = it }
                    )
                    IconButton(onClick = { navController.navigate("qr_scanner") }) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
                    }
                }
            }
            LoadingView(isLoading)
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}