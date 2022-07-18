package com.herlianzhang.mikropos.ui.expense.create_expense_category

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herlianzhang.mikropos.ui.common.DefaultSnackbar
import com.herlianzhang.mikropos.ui.common.LoadingView
import com.herlianzhang.mikropos.vo.CreateExpenseCategory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateExpenseCategoryScreen(
    navController: NavController,
    viewModel: CreateExpenseCategoryViewModel
) {
    val scaffoldState = rememberScaffoldState()
    var category by rememberSaveable {
        mutableStateOf("")
    }
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CreateExpenseCategoryEvent.ShowErrorSnackbar -> {
                    val message = event.message ?: return@collectLatest
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
                is CreateExpenseCategoryEvent.BackWithResult -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "refresh_expense_categories",
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
                    "Tambah Kategori",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        viewModel.createExpenseCategory(
                            CreateExpenseCategory( category)
                        )
                    },
                    enabled = category.isNotBlank()
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = category,
                    shape = RoundedCornerShape(12.dp),
                    label = {
                        Text("Kategori")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (category.isNotBlank()) {
                                viewModel.createExpenseCategory(
                                    CreateExpenseCategory( category)
                                )
                            }
                        }
                    ),
                    onValueChange = { category = it }
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