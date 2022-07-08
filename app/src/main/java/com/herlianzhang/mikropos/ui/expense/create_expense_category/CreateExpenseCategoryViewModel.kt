package com.herlianzhang.mikropos.ui.expense.create_expense_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ExpenseRepository
import com.herlianzhang.mikropos.vo.CreateExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateExpenseCategoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<CreateExpenseCategoryEvent>()
    val event: Flow<CreateExpenseCategoryEvent>
        get() = _event.receiveAsFlow()

    fun createExpenseCategory(data: CreateExpenseCategory) {
        viewModelScope.launch {
            expenseRepository.createExpenseCategory(data).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateExpenseCategoryEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> _event.send(CreateExpenseCategoryEvent.BackWithResult)
                }
            }
        }
    }
}