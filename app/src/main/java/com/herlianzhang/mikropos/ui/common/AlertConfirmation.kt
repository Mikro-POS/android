package com.herlianzhang.mikropos.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertConfirmation(
    showDialog: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            title = { Text(title) },
            onDismissRequest = onDismiss,
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Yakin")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Tidak")
                }
            }
        )
    }
}