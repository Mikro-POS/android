package com.herlianzhang.mikropos.ui.stock.create_stock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.AlertConfirmation
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.utils.CurrencyVisualTransformation
import com.herlianzhang.mikropos.utils.extensions.inputCurrency
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateStockScreen(
    navController: NavController,
    viewModel: CreateStockViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val isLoading by viewModel.isLoading.collectAsState()
    val localFocusManager = LocalFocusManager.current
    var showAlertConfirmation by remember { mutableStateOf(false) }
    val checkedState = remember {
        mutableStateOf(false)
    }
    var supplierName by rememberSaveable {
        mutableStateOf("")
    }
    var price by rememberSaveable {
        mutableStateOf("")
    }
    var amount by rememberSaveable {
        mutableStateOf("")
    }

    fun createStock(withChecking: Boolean = true) {
        if (withChecking && viewModel.shouldShowWarning(price.toLong())) {
            showAlertConfirmation = true
            return
        }
        viewModel.createStock(
            supplierName,
            amount.toLong(),
            price.toLong(),
            checkedState.value
        )
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CreateStockEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CreateStockEvent.BackWithResult -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_stocks",
                        true
                    )
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
                    "Catat Persediaan",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        createStock()
                    },
                    enabled = amount.toLongOrNull() != null && price.toLongOrNull() != null
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
               Row(
                   modifier = Modifier.fillMaxWidth(),
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   Text("Pengembalian Dana?")
                   Switch(
                       checked = checkedState.value,
                       onCheckedChange = {
                            checkedState.value = it
                       }
                   )
               }

               AnimatedVisibility(!checkedState.value) {
                   OutlinedTextField(
                       modifier = Modifier.fillMaxWidth(),
                       value = supplierName,
                       shape = RoundedCornerShape(12.dp),
                       label = {
                           Text("Nama Pemasok")
                       },
                       singleLine = true,
                       keyboardOptions = KeyboardOptions(
                           capitalization = KeyboardCapitalization.Words,
                           imeAction = ImeAction.Next
                       ),
                       keyboardActions = KeyboardActions(
                           onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                       ),
                       onValueChange = { supplierName = it }
                   )
               }

               OutlinedTextField(
                   modifier = Modifier.fillMaxWidth(),
                   value = price,
                   shape = RoundedCornerShape(12.dp),
                   label = {
                       Text("Harga Pembelian")
                   },
                   singleLine = true,
                   keyboardOptions = KeyboardOptions(
                       imeAction = ImeAction.Next,
                       keyboardType = KeyboardType.NumberPassword
                   ),
                   keyboardActions = KeyboardActions(
                       onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
                   ),
                   onValueChange = {
                       price = it.inputCurrency()
                   },
                   visualTransformation = CurrencyVisualTransformation()
               )

               OutlinedTextField(
                   modifier = Modifier.fillMaxWidth(),
                   value = amount,
                   shape = RoundedCornerShape(12.dp),
                   label = {
                       Text("Jumlah")
                   },
                   singleLine = true,
                   keyboardOptions = KeyboardOptions(
                       imeAction = ImeAction.Done,
                       keyboardType = KeyboardType.NumberPassword
                   ),
                   keyboardActions = KeyboardActions(
                       onDone = {
                           localFocusManager.clearFocus()
                           if (amount.toLongOrNull() != null && price.toLongOrNull() != null) {
                               createStock()
                           }
                       }
                   ),
                   onValueChange = {
                       amount = it
                   }
               )
           }
            LoadingView(isLoading)
            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            }
            AlertConfirmation(
                showDialog = showAlertConfirmation,
                title = "Catat Persediaan",
                message = "Harga Pembelian lebih tinggi daripada harga Jual.\nApakah anda yakin ingin lanjut mencatat persediaan ini?",
                onConfirm = { createStock(false) },
                onDismiss = { showAlertConfirmation = false }
            )
        }
    }
}