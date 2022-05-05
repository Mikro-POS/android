package com.herlianzhang.mikropos.ui.customerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.CustomerRepository
import com.herlianzhang.mikropos.vo.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var search: String = ""
    private var isLastPage: Boolean = false
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    private val _customers = MutableStateFlow(listOf<Customer>())
    val customers: StateFlow<List<Customer>>
        get() = _customers

    private val _isCustomerEmpty = MutableStateFlow(false)
    val isCustomerEmpty: StateFlow<Boolean>
        get() = _isCustomerEmpty

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
        getCustomers()
    }

    fun search(value: String, check: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (check) {
                delay(1000)
                if (value == search.trim())
                    return@launch
            }
            search = value.trim()
            page = 1
            isLastPage = false
            _customers.emit(emptyList())
            getCustomers()
        }
    }

    fun loadMore() {
        if (isLastPage || searchJob?.isActive == true || fetchJob?.isActive == true)
            return
        page += 1
        getCustomers()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getCustomers()
        }
    }

    private fun getCustomers() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            customerRepository.getCustomers(page, limit, search).collect { result ->
                when(result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<Customer>) {
        val curr = _customers.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isCustomerEmpty.emit(curr.isEmpty())
        _customers.emit(curr)
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