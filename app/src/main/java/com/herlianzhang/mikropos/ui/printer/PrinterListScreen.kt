package com.herlianzhang.mikropos.ui.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.herlianzhang.mikropos.ui.common.LoadingView

@Composable
fun PrinterListScreen(viewModel: PrinterListViewModel) {
    val printers by viewModel.printers.collectAsState()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsState()
    val isPrinterAvailable by viewModel.isPrinterAvailable.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.checkBluetooth()
                viewModel.getPrinters()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }
    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = {  }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                }
                Text(
                    "Printer",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
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
            if (isBluetoothOn) {
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
            if (!isBluetoothOn) {
                TextButton(onClick = { launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) }) {
                    Text("Hidupkan Bluetooth")
                }
            }
            LoadingView(isLoading)
        }
    }
}