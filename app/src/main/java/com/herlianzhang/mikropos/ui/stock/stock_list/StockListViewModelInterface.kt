package com.herlianzhang.mikropos.ui.stock.stock_list

import com.herlianzhang.mikropos.vo.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface StockListViewModelInterface {
    val stocks: StateFlow<List<Stock>>
    val isProductEmpty: StateFlow<Boolean>
    val isLoading: StateFlow<Boolean>
    val isLoadMore: StateFlow<Boolean>
    val isError: StateFlow<Boolean>
    val event: Flow<StockListEvent>

    fun loadMore()
    fun setData(productJSON: String)
    fun tryAgain()
    suspend fun refresh()
    fun deleteStock(stockId: Int)
}