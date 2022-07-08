package com.herlianzhang.mikropos.ui.expense.create_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ExpenseRepository
import com.herlianzhang.mikropos.ui.product.create_product.CreateProductEvent
import com.herlianzhang.mikropos.vo.CreateExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<CreateExpenseEvent>()
    val event: Flow<CreateExpenseEvent>
        get() = _event.receiveAsFlow()

    fun createExpense(data: CreateExpense) {
        viewModelScope.launch {
            expenseRepository.createExpense(data).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateExpenseEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> {
                        _event.send(CreateExpenseEvent.ShowErrorSnackbar("Pengeluaran berhasil dibuat"))
                        delay(500)
                        _event.send(CreateExpenseEvent.ClearUserInput)
                    }
                }
            }
        }
    }
}