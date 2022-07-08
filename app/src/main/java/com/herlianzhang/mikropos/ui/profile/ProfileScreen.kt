package com.herlianzhang.mikropos.ui.profile

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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
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
import com.herlianzhang.mikropos.vo.UpdateUser
import com.herlianzhang.mikropos.vo.UserKey
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

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
                is ProfileEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is ProfileEvent.HideDialog -> {
                    isShowDialog = false
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
                    "Ubah Profil",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(48.dp))
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
                            model = data.logo,
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

                    DetailItem(key = "Username", value = data.username)
                    DetailItem(key = "Nama", value = data.name) {
                        dialogKey = UserKey.NAME.getValue()
                        dialogValue = data.name ?: "-"
                        dialogTitle = "Ubah Nama"
                        dialogType = EditDialogType.Default
                        isShowDialog = true
                    }
                    DetailItem(key = "Alamat", value = data.address) {
                        dialogKey = UserKey.ADDRESS.getValue()
                        dialogValue = data.address ?: "-"
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
                isDismiss = isShowDialog,
                onDismiss = {
                    viewModel.cancelUpdateUser()
                    isShowDialog = false
                },
                onSubmit = {
                    viewModel.updateUser(
                        UpdateUser.update(
                            key = UserKey.fromKey(dialogKey),
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
        }
    }
}