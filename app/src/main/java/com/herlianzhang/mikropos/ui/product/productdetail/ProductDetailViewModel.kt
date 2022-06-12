package com.herlianzhang.mikropos.ui.product.productdetail

import android.app.Activity
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herlianzhang.mikropos.MainActivity
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ImageRepository
import com.herlianzhang.mikropos.repository.ProductRepository
import com.herlianzhang.mikropos.ui.stock.createstock.CreateStockViewModel
import com.herlianzhang.mikropos.utils.extensions.getImageDisplayName
import com.herlianzhang.mikropos.vo.ProductDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailViewModel @AssistedInject constructor(
    @Assisted
    val id: Int,
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository,
    app: Application
): AndroidViewModel(app) {
    private var updateJob: Job? = null
    private var uploadJob: Job? = null

    private val _data = MutableStateFlow<ProductDetail?>(null)
    val data: StateFlow<ProductDetail?>
        get() = _data

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean>
        get() = _isUploadingImage

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

    init {
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

    fun updateProduct(params: Map<String, Any>, fromDialog: Boolean = true) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            productRepository.updateProduct(id, params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> {
                        if (fromDialog)
                            _isDialogLoading.emit(result.state.value)
                        else
                            _isUploadingImage.emit(result.state.value)
                    }
                    is ApiResult.Success -> {
                        _event.send(ProductDetailEvent.SetHasChanges)
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

    fun deleteProduct() {
        viewModelScope.launch {
            productRepository.deleteProduct(id).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> {
                        _event.send(ProductDetailEvent.SetHasChanges)
                        _event.send(ProductDetailEvent.Back)
                    }
                    is ApiResult.Failed -> _event.send(ProductDetailEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }

    fun uploadImage(uri: Uri) {
        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            imageRepository.uploadImage(uri, getImageDisplayName(uri)).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isUploadingImage.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(ProductDetailEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> {
                        result.data?.url?.let { url ->
                            val params = mapOf("photo" to url)
                            updateProduct(params, false)
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): ProductDetailViewModel
    }

    companion object {
        private fun providesFactory(
            assistedFactory: Factory,
            productId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(productId) as T
            }
        }

        @Composable
        fun getViewModel(id: Int): ProductDetailViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).productDetailViewModelFactory()
            return viewModel(factory = providesFactory(factory, id))
        }
    }
}