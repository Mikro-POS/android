package com.herlianzhang.mikropos.ui.expense.create_expense

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Event
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.DropDown
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.utils.CurrencyVisualTransformation
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.utils.extensions.inputCurrency
import com.herlianzhang.mikropos.vo.CreateExpense
import com.herlianzhang.mikropos.vo.ExpenseCategory
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun CreateExpenseScreen(
    navController: NavController,
    viewModel: CreateExpenseViewModel
) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedCategory: ExpenseCategory? by rememberSaveable {
        mutableStateOf(null)
    }
    var date: Long? by rememberSaveable {
        mutableStateOf(null)
    }
    var nominal by rememberSaveable {
        mutableStateOf("")
    }
    var desc by rememberSaveable {
        mutableStateOf("")
    }

    val mCalendar by rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }
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
            date = mCalendar.timeInMillis
        }, mYear, mMonth, mDay
    ).also { dialog ->
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.set(Calendar.MINUTE, 0)
        tmpCalendar.set(Calendar.SECOND, 0)
        tmpCalendar.set(Calendar.MILLISECOND, 0)
        tmpCalendar.set(Calendar.DAY_OF_MONTH, 1)
        dialog.datePicker.minDate = tmpCalendar.timeInMillis
        tmpCalendar.add(Calendar.MONTH, 1)
        tmpCalendar.add(Calendar.DAY_OF_MONTH, -1)
        dialog.datePicker.maxDate = tmpCalendar.timeInMillis
    }

    navController.currentBackStackEntry?.savedStateHandle?.let { savedState ->
        savedState.getLiveData<ExpenseCategory>("selected_category").let {
            val selected by it.observeAsState()
            LaunchedEffect(selected) {
                selected ?: return@LaunchedEffect
                selectedCategory = selected
                savedState.remove<ExpenseCategory>("selected_category")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CreateExpenseEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CreateExpenseEvent.ClearUserInput -> {
                    selectedCategory = null
                    date = null
                    nominal = ""
                    desc = ""
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
                    "Catat Pengeluaran",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        viewModel.createExpense(
                            CreateExpense(
                                categoryId = selectedCategory?.id ?: 0,
                                nominal = nominal.toLong(),
                                description = desc,
                                date = date ?: 0
                            )
                        )
                    },
                    enabled = selectedCategory != null && date != null && nominal.toLongOrNull() != null
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

                DropDown(
                    selectedCategory?.name ?: "Pilih Kategori",
                    selectedCategory != null
                ) {
                    navController.navigate("expense_categories")
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        date?.formatDate("dd MMMM yyyy") ?: "Pilih tanggal",
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (date != null) Color.Black else Color.Gray,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        color = if (date != null) Color.Black else Color.Gray
                    )

                    IconButton(onClick = {
                        mDatePickerDialog.show()
                    }) {
                        Icon(Icons.Rounded.Event, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = nominal,
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Nominal")
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
                        nominal = it.inputCurrency()
                    },
                    visualTransformation = CurrencyVisualTransformation()
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = desc,
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Keterangan")
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (selectedCategory != null && date != null && nominal.toLongOrNull() != null) {
                                viewModel.createExpense(
                                    CreateExpense(
                                        categoryId = selectedCategory?.id ?: 0,
                                        nominal = nominal.toLong(),
                                        description = desc,
                                        date = date ?: 0
                                    )
                                )
                            }
                        }
                    ),
                    onValueChange = { desc = it }
                )
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