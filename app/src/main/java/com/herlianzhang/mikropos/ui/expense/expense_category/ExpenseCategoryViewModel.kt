package com.herlianzhang.mikropos.ui.expense.expense_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.ExpenseRepository
import com.herlianzhang.mikropos.vo.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseCategoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var isLastPage: Boolean = false
    private var fetchJob: Job? = null

    private val _categories = MutableStateFlow(listOf<ExpenseCategory>())
    val categories: StateFlow<List<ExpenseCategory>>
        get() = _categories

    private val _isCategoryEmpty = MutableStateFlow(false)
    val isCategoryEmpty: StateFlow<Boolean>
        get() = _isCategoryEmpty

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoadMore = MutableStateFlow(false)
    val isLoadMore: StateFlow<Boolean>
        get() = _isLoadMore

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    init {
        getCategories()
    }

    fun loadMore() {
        if (isLastPage || fetchJob?.isActive == true)
            return
        page += 1
        getCategories()
    }

    fun refresh() {
        viewModelScope.launch {
            fetchJob?.cancel()
            page = 1
            isLastPage = false
            _categories.emit(listOf())
            getCategories()
        }
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getCategories()
        }
    }

    private fun getCategories() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            expenseRepository.getExpenseCategories(page, limit).collect { result ->
                when(result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<ExpenseCategory>) {
        val curr = _categories.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isCategoryEmpty.emit(curr.isEmpty())
        _categories.emit(curr)
    }

    private suspend fun handleError() {
        _isError.emit(true)
    }

    private suspend fun handleLoading(state: LoadingState) {
        when (state) {
            is LoadingState.Start -> {
                if (page == 1)
                    _isLoading.emit(page == 1)
            }
            is LoadingState.End -> {
                _isLoadMore.emit(!isLastPage)
                _isLoading.emit(false)
            }
        }
    }
}