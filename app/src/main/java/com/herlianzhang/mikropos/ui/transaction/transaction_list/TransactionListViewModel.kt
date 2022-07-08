package com.herlianzhang.mikropos.ui.transaction.transaction_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.TransactionRepository
import com.herlianzhang.mikropos.utils.extensions.formatDate
import com.herlianzhang.mikropos.vo.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var isLastPage: Boolean = false
    private var fetchJob: Job? = null
    private var startDate: Long? = null
    private var endDate: Long? = null

    private val _transactions = MutableStateFlow(listOf<Transaction>())
    val transactions: StateFlow<List<Transaction>>
        get() = _transactions

    private val _isTransactionEmpty = MutableStateFlow(false)
    val isTransactionEmpty: StateFlow<Boolean>
        get() = _isTransactionEmpty

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoadMore = MutableStateFlow(false)
    val isLoadMore: StateFlow<Boolean>
        get() = _isLoadMore

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    private val _filterWording: MutableStateFlow<String?> = MutableStateFlow(null)
    val filterWording: StateFlow<String?>
        get() = _filterWording

    private val _isBillingPeriod = MutableStateFlow(false)
    val isBillingPeriod: StateFlow<Boolean>
        get() = _isBillingPeriod

    private val _isNotYetPaidOff = MutableStateFlow(false)
    val isNotYetPaidOff: StateFlow<Boolean>
        get() = _isNotYetPaidOff

    init {
        getTransactions()
    }

    fun loadMore() {
        if (isLastPage  || fetchJob?.isActive == true)
            return
        page += 1
        getTransactions()
    }

    fun refresh() {
        viewModelScope.launch {
            page = 1
            isLastPage = false
            _transactions.emit(emptyList())
            getTransactions()
        }
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getTransactions()
        }
    }

    fun filterDate(startDate: Long?, endDate: Long?) {
        if (this.startDate == startDate && this.endDate == endDate) return
        this.startDate = startDate
        this.endDate = endDate
        val wording = when {
            startDate != null && endDate != null -> {
                "${startDate.formatDate("dd-MM-yy")} s.d ${startDate.formatDate("dd-MM-yy")}"
            }
            startDate != null -> {
                "Dari ${startDate.formatDate("dd-MM-yy")}"
            }
            endDate != null -> {
                "Sampai ${endDate.formatDate("dd-MM-yy")}"
            }
            else -> null
        }
        viewModelScope.launch {
            refresh()
            _filterWording.emit(wording)
        }
    }

    fun filterBillingPeriod() {
        val value = _isBillingPeriod.value
        viewModelScope.launch {
            refresh()
            _isBillingPeriod.emit(!value)
        }
    }

    fun filterNotYetPaidOff() {
        val value = _isNotYetPaidOff.value
        viewModelScope.launch {
            refresh()
            _isNotYetPaidOff.emit(!value)
        }
    }

    private fun getTransactions() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            transactionRepository.getTransactions(
                limit,
                page,
                startDate,
                endDate,
                _isBillingPeriod.value,
                _isNotYetPaidOff.value
            ).collect { result ->
                when (result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<Transaction>) {
        val curr = _transactions.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isTransactionEmpty.emit(curr.isEmpty())
        _transactions.emit(curr)
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