package com.herlianzhang.mikropos.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PrinterAlert(
    navController: NavController,
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            title = { Text("Printer tidak terhubung") },
            onDismissRequest = onDismiss,
            text = { Text("Perangkat anda belum terhubung dengan printer, apakah anda yakin ingin lanjut?") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Yakin")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    navController.navigate("printer_list")
                    onDismiss()
                }) {
                    Text("Buka Setting")
                }
            }
        )
    }
}