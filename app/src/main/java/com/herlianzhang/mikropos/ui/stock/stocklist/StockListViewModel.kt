package com.herlianzhang.mikropos.ui.stock.stocklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.StockRepository
import com.herlianzhang.mikropos.vo.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockListViewModel @Inject constructor(
    private val stockRepository: StockRepository
): ViewModel() {
    private var productId: Int = 0
    private val limit: Int = 20
    private var page: Int = 1
    private var isLastPage: Boolean = false
    private var fetchJob: Job? = null

    private val _stocks = MutableStateFlow(listOf<Stock>())
    val stocks: StateFlow<List<Stock>>
        get() = _stocks

    private val _isStockEmpty = MutableStateFlow(false)
    val isProductEmpty: StateFlow<Boolean>
        get() = _isStockEmpty

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoadMore = MutableStateFlow(false)
    val isLoadMore: StateFlow<Boolean>
        get() = _isLoadMore

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    fun setProductId(id: Int) {
        this.productId = id
        getStocks()
    }

    fun loadMore() {
        if (isLastPage || fetchJob?.isActive == true)
            return
        page += 1
        getStocks()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getStocks()
        }
    }

    suspend fun refresh() {
        page = 1
        isLastPage = false
        _stocks.emit(emptyList())
        getStocks()
    }

    private fun getStocks() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            stockRepository.getStocks(productId, page, limit).collect { result ->
                when(result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<Stock>) {
        val curr = _stocks.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isStockEmpty.emit(curr.isEmpty())
        _stocks.emit(curr)
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