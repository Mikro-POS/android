package com.herlianzhang.mikropos.ui.stock.stock_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.StockRepository
import com.herlianzhang.mikropos.utils.UserPreferences
import com.herlianzhang.mikropos.utils.extensions.getPrinter
import com.herlianzhang.mikropos.utils.extensions.printFormattedText
import com.herlianzhang.mikropos.utils.extensions.printValue
import com.herlianzhang.mikropos.vo.ProductDetail
import com.herlianzhang.mikropos.vo.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockListViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val userPref: UserPreferences,
    private val gson: Gson
) : ViewModel(), StockListViewModelInterface {
    private val limit: Int = 20
    private var page: Int = 1
    private var isLastPage: Boolean = false
    private var fetchJob: Job? = null
    private var product: ProductDetail? = null

    private val _stocks = MutableStateFlow(listOf<Stock>())
    override val stocks: StateFlow<List<Stock>>
        get() = _stocks

    private val _isStockEmpty = MutableStateFlow(false)
    override val isProductEmpty: StateFlow<Boolean>
        get() = _isStockEmpty

    private val _isLoading = MutableStateFlow(true)
    override val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoadMore = MutableStateFlow(false)
    override val isLoadMore: StateFlow<Boolean>
        get() = _isLoadMore

    private val _isError = MutableStateFlow(false)
    override val isError: StateFlow<Boolean>
        get() = _isError

    private val _event = Channel<StockListEvent>()
    override val event: Flow<StockListEvent>
        get() = _event.receiveAsFlow()

    override fun loadMore() {
        if (isLastPage || fetchJob?.isActive == true)
            return
        page += 1
        getStocks()
    }

    override fun setData(productJSON: String) {
        if (product != null) return
        product = gson.fromJson(productJSON, ProductDetail::class.java)
        getStocks()
    }

    override fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getStocks()
        }
    }

    fun printStock(item: Stock) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            try {
                val printer = userPref.getPrinter()
                printer.printFormattedText(item.printValue(product!!))
            } catch (e: Exception) {
                _event.send(StockListEvent.ShowErrorSnackbar(e.message))
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    override suspend fun refresh() {
        page = 1
        isLastPage = false
        _stocks.emit(emptyList())
        getStocks()
    }

    private fun getStocks() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            stockRepository.getStocks(product?.id ?: 0, page, limit).collect { result ->
                when (result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    override fun deleteStock(stockId: Int) {
        viewModelScope.launch {
            stockRepository.deleteStock(product?.id ?: 0, stockId).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        refresh()
                        _event.send(StockListEvent.SendResult)
                    }
                    is ApiResult.Failed -> _event.send(StockListEvent.ShowErrorSnackbar(result.message))
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