package com.herlianzhang.mikropos.ui.product.productlist

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
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.repository.CartRepository
import com.herlianzhang.mikropos.repository.ProductRepository
import com.herlianzhang.mikropos.vo.Product
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class ProductListViewModel @AssistedInject constructor(
    @Assisted
    private val isSelectMode: Boolean,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
): ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var search: String = ""
    private var isLastPage: Boolean = false
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    private val _event = Channel<ProductListEvent>()
    val event: Flow<ProductListEvent>
        get() = _event.receiveAsFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String>
        get() = _title

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
        viewModelScope.launch {
            _title.emit(if (isSelectMode) "Pilih Produk" else "Daftar Produk")
        }
        getProducts()
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
            _products.emit(emptyList())
            getProducts()
        }
    }

    fun loadMore() {
        if (isLastPage || searchJob?.isActive == true || fetchJob?.isActive == true)
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

    fun onClickProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSelectMode) {
                val dbCart = cartRepository.getCartById(product.id)
                val cart = Cart(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    photo = product.photo,
                    amount = dbCart?.amount?.plus(1) ?: 1
                )
                cartRepository.insertCart(cart)
            } else {
                withContext(Dispatchers.Main) {
                    _event.send(ProductListEvent.NavigateToProductDetail(product.id))
                }
            }
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

    @AssistedFactory
    interface Factory {
        fun create(isSelectMode: Boolean): ProductListViewModel
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
        fun getViewModel(isSelectMode: Boolean): ProductListViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).productListViewModelFactory()
            return viewModel(factory = providesFactory(factory, isSelectMode))
        }
    }
}