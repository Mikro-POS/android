package com.herlianzhang.mikropos.ui.expense.create_expense

sealed class CreateExpenseEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateExpenseEvent()
    object ClearUserInput : CreateExpenseEvent()
}