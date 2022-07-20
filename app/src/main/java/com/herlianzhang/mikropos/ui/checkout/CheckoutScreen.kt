package com.herlianzhang.mikropos.ui.checkout

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Event
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.AlertConfirmation
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.DropDown
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.CreateTransaction
import com.herlianzhang.mikropos.vo.CreateTransactionItem
import com.herlianzhang.mikropos.vo.Customer
import com.herlianzhang.mikropos.vo.TransactionStatus
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel
) {
    val maxInstallment = 10
    val context = LocalContext.current
    val carts = viewModel.carts.collectAsState(emptyList())
    val totalPrice = viewModel.totalPrice.collectAsState(0)
    val isLoading by viewModel.isLoading.collectAsState()
    val scaffoldState = rememberScaffoldState()
    var showAlertConfirmation by remember { mutableStateOf(false) }
    var checkedState by rememberSaveable {
        mutableStateOf(true)
    }
    var selectedCustomer: Customer? by rememberSaveable {
        mutableStateOf(null)
    }
    var dueDate: Long? by rememberSaveable {
        mutableStateOf(null)
    }
    val mCalendar by rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }
    var totalInstallment: Int? by rememberSaveable {
        mutableStateOf(null)
    }
    var expanded by remember { mutableStateOf(false)}

    val mYear: Int = mCalendar.get(Calendar.YEAR)
    val mMonth: Int = mCalendar.get(Calendar.MONTH)
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            mCalendar.set(Calendar.HOUR_OF_DAY, 0)
            mCalendar.set(Calendar.MINUTE, 0)
            mCalendar.set(Calendar.SECOND, 0)
            mCalendar.set(Calendar.MILLISECOND, 0)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            dueDate = mCalendar.timeInMillis
        }, mYear, mMonth, mDay
    ).also {  dialog ->
        dialog.datePicker.minDate = System.currentTimeMillis() + 24*60*60*1000
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CheckoutEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CheckoutEvent.NavigateToDetail -> {
                    navController.navigate("transaction/${event.id}") {
                        popUpTo("checkout") {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<Customer>("selected_customer").let {
            val selected by it.observeAsState()
            LaunchedEffect(selected) {
                selected ?: return@LaunchedEffect
                selectedCustomer = selected
                savedState.remove<Customer>("selected_customer")
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
                    "Pembayaran",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(48.dp))
            }
        }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Kasbon?",
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = checkedState,
                                    onCheckedChange = {
                                        checkedState = it
                                    }
                                )
                            }

                            Spacer(Modifier.height(4.dp))

                            Text("Nama Pelanggan")

                            DropDown(
                                selectedCustomer?.name ?: "Pilih Pelanggan",
                                selectedCustomer != null
                            ) {
                                navController.navigate("select_customer")
                            }

                            if (checkedState) {
                                Spacer(Modifier.height(4.dp))

                                Text("Jatuh Tempo")

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        dueDate?.formatDate("dd MMMM yyyy") ?: "Tanggal jatuh tempo",
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(
                                                1.dp,
                                                if (dueDate != null) Color.Black else Color.Gray,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(16.dp),
                                        color = if (dueDate != null) Color.Black else Color.Gray
                                    )

                                    IconButton(onClick = {
                                        mDatePickerDialog.show()
                                    }) {
                                        Icon(Icons.Rounded.Event, contentDescription = null)
                                    }
                                }

                                Spacer(Modifier.height(4.dp))

                                Text("Berapa kali pembayaran")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    DropDown(
                                        if (totalInstallment != null) "$totalInstallment Kali" else "Pilih Total Cicilan",
                                        totalInstallment != null
                                    ) {
                                        expanded = true
                                    }
                                    DropdownMenu(
                                        modifier = Modifier.requiredSizeIn(maxHeight = 200.dp),
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        for (i in 0 until maxInstallment) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    totalInstallment = i + 1
                                                    expanded = false
                                                }
                                            ) {
                                                Text("${i + 1} Kali")
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text("Detail Belanjaan")
                        }
                    }

                    items(carts.value) { item ->
                        CheckoutItem(item)
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = selectedCustomer != null && (!checkedState || (dueDate != null && totalInstallment != null)),
                    shape = CircleShape,
                    onClick = { showAlertConfirmation = true }
                ) {
                    Text(totalPrice.value.toRupiah())
                }
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
                title = "Konfirmasi Pembayaran",
                message = "Apakah anda yakin ingin melanjutkan transaksi ini?\ntransaksi yang sudah dibuat tidak dapat dihapus lagi!",
                onConfirm = {
                    val data = CreateTransaction(
                        customerId = selectedCustomer?.id,
                        status = if (checkedState) TransactionStatus.DEBT else TransactionStatus.COMPLETED,
                        totalInstallment = totalInstallment,
                        debtDue = dueDate,
                        items = carts.value.map { CreateTransactionItem(it.id, it.amount) }
                    )
                    viewModel.createTransaction(data)
                },
                onDismiss = { showAlertConfirmation = false }
            )
        }
    }
}