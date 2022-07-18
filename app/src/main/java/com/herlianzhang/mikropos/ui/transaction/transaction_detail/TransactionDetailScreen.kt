package com.herlianzhang.mikropos.ui.transaction.transaction_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Print
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.*
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.toRupiah
import com.herlianzhang.mikropos.vo.TransactionStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    viewModel: TransactionDetailViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val data by viewModel.data.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val isNotFound by viewModel.isNotFound.collectAsState()
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isShowStatusDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val isDialogLoading by viewModel.isDialogLoading.collectAsState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is TransactionDetailEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is TransactionDetailEvent.HideDialog -> {
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
                    "Detail Transaksi",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { viewModel.printTransaction() },
                    enabled = !isLoading && data != null
                ) {
                    Icon(Icons.Rounded.Print, contentDescription = null)
                }
            }
        }
    ) {
        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = null)
                        }

                        Text(
                            "Lainnya",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Cetak Tiket Pesanan",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                    viewModel.printOrderTicket()
                                }
                            },
                    )

                    Text(
                        "Bantuan Pengambilan Persediaan",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                data?.items?.let { items ->
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "transactionItems",
                                        items
                                    )
                                    navController.navigate("transaction/stock_help")
                                }
                            },
                    )

                    if (data?.status == TransactionStatus.DEBT) {
                        Text(
                            "Tandai Sebagai Hilang",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        isShowStatusDialog = true
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                },
                        )
                    }
                }
            },
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 0.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            contentColor = Color.Black.copy(alpha = 0.3f)
        ) {
            Box {
                data?.let { data ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp, bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Row {
                                        Text(
                                            text = "Status",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = data.status.value,
                                            fontWeight = FontWeight.Bold,
                                            color = data.status.color,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    DetailItem(key = "Nama Pelanggan", value = data.customer?.name, paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                    DetailItem(key = "Tanggal Pembelian", value = data.createdAt.formatDate(), paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                    Spacer(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.LightGray)
                                    )
                                    Text(
                                        "Detail Produk",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            items(data.items) { item ->
                                TransactionDetailItem(item)
                            }

                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.LightGray)
                                    )
                                    Text(
                                        "Rincian Pembayaran",
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (data.status == TransactionStatus.DEBT) {
                                        DetailItem(key = "Jatuh Tempo", value = data.debtDue?.formatDate("dd MMMM yyyy"), paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                        DetailItem(key = "Cicilan ke", value = "${data.currentInstallment} dari ${data.totalInstallment}", paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                        DetailItem(key = "Sisa Hutang", value = data.currentDebt.toRupiah(), paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                        Spacer(
                                            modifier = Modifier
                                                .height(1.dp)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.LightGray)
                                        )
                                    }
                                    DetailItem(key = "Estimasi Keuntungan", value = data.totalProfit.toRupiah(), paddingHorizontal = 0.dp, paddingVertical = 0.dp, valueAlignment = TextAlign.End)
                                    Row {
                                        Text(
                                            text = "Total Belanja",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = data.totalPrice.toRupiah(),
                                            style = MaterialTheme.typography.h6,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }
                        }
                        Column {
                            Spacer(
                                modifier = Modifier
                                    .height(3.dp)
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.4f)
                                            )
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .border(1.dp, Color.Black, CircleShape),
                                    onClick = {
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.MoreHoriz, contentDescription = null)
                                }
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = CircleShape,
                                    onClick = {
                                        if (data.status == TransactionStatus.DEBT) {
                                            isShowDialog = true
                                        } else {
                                            viewModel.printTransaction()
                                        }
                                    }
                                ) {
                                    Text(if (data.status == TransactionStatus.DEBT) "Bayar Cicilan" else "Cetak")
                                }
                            }
                        }

                    }
                }
                if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(0.3f))
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                    )
                }
                LoadingView(isLoading)
                ErrorView(isError, onClick = { viewModel.tryAgain() })
                PayDialog(
                    isLoading = isDialogLoading,
                    isDismiss = isShowDialog,
                    onDismiss = {
                        viewModel.cancelPayInstallments()
                        isShowDialog = false
                    },
                    onSubmit = { viewModel.payInstallments(it) }
                )
                AlertConfirmation(
                    showDialog = isShowStatusDialog,
                    title = "Tandai Sebagai Hilang",
                    message = "Apakah anda yakin ingin menandai transaksi ini sebagai hilang?",
                    onDismiss = {
                        isShowStatusDialog = false
                    },
                    onConfirm = {
                        viewModel.changeTransactionStatusToLost()
                    }
                )
                DefaultSnackbar(
                    snackbarHostState = scaffoldState.snackbarHostState,
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                }
                NotFoundView(isNotFound)
            }
        }
    }
}