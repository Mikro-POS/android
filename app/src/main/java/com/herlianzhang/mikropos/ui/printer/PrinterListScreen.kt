package com.herlianzhang.mikropos.ui.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.PrintDisabled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.EmptyView
import com.herlianzhang.mikropos.ui.common.LoadingView
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrinterListScreen(
    navController: NavController,
    viewModel: PrinterListViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val printers by viewModel.printers.collectAsState()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsState()
    val isPrinterAvailable by viewModel.isPrinterAvailable.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPrinterEmpty by viewModel.isPrinterEmpty.collectAsState()

    val permissionState = rememberMultiplePermissionsState(
        mutableListOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
        ).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                it.add(android.Manifest.permission.BLUETOOTH_CONNECT)
                it.add(android.Manifest.permission.BLUETOOTH_SCAN)
            }
        }
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.checkBluetooth()
                viewModel.getPrinters()
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is PrinterListEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Printer",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    enabled = !isLoading && isPrinterAvailable,
                    onClick = {
                        viewModel.printTest()
                    }
                ) {
                    Icon(
                        if (isPrinterAvailable) Icons.Rounded.Print else Icons.Rounded.PrintDisabled,
                        contentDescription = null
                    )
                }
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (permissionState.allPermissionsGranted) {
                if (isBluetoothOn) {
                    SwipeRefresh(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = rememberSwipeRefreshState(isLoading),
                        onRefresh = { viewModel.getPrinters() }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(printers) { item ->
                                PrinterItem(item) {
                                    viewModel.connect(item.device.address ?: null)
                                }
                            }
                        }
                    }
                }
                if (!isBluetoothOn) {
                    LaunchedEffect(true) {
                        launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                    TextButton(onClick = { launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) }) {
                        Text("Aktifkan Bluetooth")
                    }
                }
            } else {
                LaunchedEffect(true) {
                    permissionState.launchMultiplePermissionRequest()
                }
                TextButton(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                    Text("Izinkan izin bluetooth")
                }
            }
            EmptyView(isPrinterEmpty)
            LoadingView(isLoading)
        }
    }
}