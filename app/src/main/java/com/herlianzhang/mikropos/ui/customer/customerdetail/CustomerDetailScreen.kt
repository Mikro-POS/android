package com.herlianzhang.mikropos.ui.customer.customerdetail

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.vo.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CustomerDetailScreen(
    navController: NavController,
    viewModel: CustomerDetailViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val isNotFound by viewModel.isNotFound.collectAsState()
    var showAlertConfirmation by remember { mutableStateOf(false) }
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

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CustomerDetailEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CustomerDetailEvent.HideDialog -> {
                    isShowDialog = false
                }
                is CustomerDetailEvent.SetHasChanges -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_customers",
                        true
                    )
                }
                is CustomerDetailEvent.Back -> {
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
                    "Detail Pelanggan",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = { showAlertConfirmation = true },
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
                        dialogKey = CustomerKey.NAME.getValue()
                        dialogValue = data.name ?: ""
                        dialogTitle = "Ubah Nama"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
                    }
                    DetailItem(key = "Nomor Telepon 1", value = data.phoneNumber) {
                        dialogKey = CustomerKey.PHONE_NUMBER_1.getValue()
                        dialogValue = data.phoneNumber ?: ""
                        dialogTitle = "Ubah Nomor Telepon 1"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
                    }
                    DetailItem(key = "Nomor Telepon 2", value = data.phoneNumber2) {
                        dialogKey = CustomerKey.PHONE_NUMBER_2.getValue()
                        dialogValue = data.phoneNumber2 ?: ""
                        dialogTitle = "Ubah Nomor Telepon 2"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
                    }
                    DetailItem(key = "Alamat", value = data.address) {
                        dialogKey = CustomerKey.ADDRESS.getValue()
                        dialogValue = data.address ?: ""
                        dialogTitle = "Ubah Alamat"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
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
                keyboardType = if (dialogKey == "phone_number_1" || dialogKey == "phone_number_2") KeyboardType.Phone else null,
                isDismiss = isShowDialog,
                onDismiss = {
                    viewModel.cancelUpdateCustomer()
                    isShowDialog = false
                },
                onSubmit = {
                    viewModel.updateCustomer(
                        CreateOrUpdateCustomer.update(
                            key = CustomerKey.fromKey(dialogKey),
                            value = dialogValue
                        )
                    )
                }
            )
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
            NotFoundView(isNotFound)
            AlertConfirmation(
                showDialog = showAlertConfirmation,
                title = "Hapus",
                message = "Apakah anda yakin ingin menghapus Pelanggan ini?",
                onConfirm = { viewModel.deleteCustomer() },
                onDismiss = { showAlertConfirmation = false }
            )
        }
    }
}