package com.herlianzhang.mikropos.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.ProductRepository
import com.herlianzhang.mikropos.vo.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var search: String = ""
    private var isLastPage: Boolean = false
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    private val _products = MutableStateFlow(listOf<Product>())
    val products: StateFlow<List<Product>>
        get() = _products

    private val _isProductEmpty = MutableStateFlow(false)
    val isProductEmpty: StateFlow<Boolean>
        get() = _isProductEmpty

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
        getProducts()
    }

    fun search(value: String, check: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (check) {
                delay(1000)
                if (value == search)
                    return@launch
            }
            search = value
            page = 1
            isLastPage = false
            _products.emit(emptyList())
            getProducts()
        }
    }

    fun loadMore() {
        if (isLastPage || searchJob?.isActive == true)
            return
        page += 1
        getProducts()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getProducts()
        }
    }

    private fun getProducts() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            productRepository.getProducts(page, limit, search).collect { result ->
                when(result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<Product>) {
        val curr = _products.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isProductEmpty.emit(curr.isEmpty())
        _products.emit(curr)
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