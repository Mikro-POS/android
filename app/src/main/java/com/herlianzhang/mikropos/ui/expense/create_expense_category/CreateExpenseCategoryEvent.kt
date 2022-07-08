package com.herlianzhang.mikropos.ui.expense.create_expense_category

sealed class CreateExpenseCategoryEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateExpenseCategoryEvent()
    object BackWithResult : CreateExpenseCategoryEvent()
}