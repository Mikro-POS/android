package com.herlianzhang.mikropos.ui.customer.customerlist

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herlianzhang.mikropos.MainActivity
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.CustomerRepository
import com.herlianzhang.mikropos.vo.Customer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CustomerListViewModel @AssistedInject constructor(
    @Assisted
    private val isSelectMode: Boolean,
    private val customerRepository: CustomerRepository
) : ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var search: String = ""
    private var isLastPage: Boolean = false
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    private val _event = Channel<CustomerListEvent>()
    val event: Flow<CustomerListEvent>
        get() = _event.receiveAsFlow()

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

    fun onClickCustomer(customer: Customer) {
        viewModelScope.launch {
            if (isSelectMode) {
                _event.send(CustomerListEvent.BackWithResult(customer))
            } else {
                _event.send(CustomerListEvent.NavigateToCustomerDetail(customer.id))
            }
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

    @AssistedFactory
    interface Factory {
        fun create(isSelectMode: Boolean): CustomerListViewModel
    }

    companion object {
        private fun providesFactory(
            assistedFactory: Factory,
            isSelectMode: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(isSelectMode) as T
            }
        }

        @Composable
        fun getViewModel(isSelectMode: Boolean): CustomerListViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).customerListViewModelFactory()
            return viewModel(factory = providesFactory(factory, isSelectMode))
        }
    }
}