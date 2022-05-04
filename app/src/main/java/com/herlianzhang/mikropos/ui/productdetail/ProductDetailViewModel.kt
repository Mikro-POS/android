package com.herlianzhang.mikropos.ui.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ProductRepository
import com.herlianzhang.mikropos.vo.ProductDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProductDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : ProductDetailEvent()
    object HideDialog : ProductDetailEvent()
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    private var id: Int = 0
    private var updateJob: Job? = null

    private val _data = MutableStateFlow<ProductDetail?>(null)
    val data: StateFlow<ProductDetail?>
        get() = _data

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    private val _isNotFound = MutableStateFlow(false)
    val isNotFound: StateFlow<Boolean>
        get() = _isNotFound

    private val _isDialogLoading = MutableStateFlow(false)
    val isDialogLoading: StateFlow<Boolean>
        get() = _isDialogLoading

    private val _event = Channel<ProductDetailEvent>()
    val event: Flow<ProductDetailEvent>
        get() = _event.receiveAsFlow()

    fun setProductId(id: Int) {
        this.id = id
        getProduct()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getProduct()
        }
    }

    private fun getProduct() {
        viewModelScope.launch {
            productRepository.getProduct(id).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> _data.emit(result.data)
                    is ApiResult.Failed -> {
                        when(result.code) {
                            404 -> _isNotFound.emit(true)
                            else -> _isError.emit(true)
                        }
                    }
                }
            }
        }
    }

    fun updateProduct(params: Map<String, Any>) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            productRepository.updateProduct(id, params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isDialogLoading.emit(result.state.value)
                    is ApiResult.Success -> {
                        _event.send(ProductDetailEvent.HideDialog)
                        _data.emit(result.data)
                    }
                    is ApiResult.Failed -> _event.send(ProductDetailEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }

    fun cancelUpdateProduct() {
        viewModelScope.launch {
            _isDialogLoading.emit(false)
            updateJob?.cancel()
        }
    }
}