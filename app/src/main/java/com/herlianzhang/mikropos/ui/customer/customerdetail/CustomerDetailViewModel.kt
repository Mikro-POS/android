package com.herlianzhang.mikropos.ui.customer.customerdetail

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
import com.herlianzhang.mikropos.repository.CustomerRepository
import com.herlianzhang.mikropos.repository.ImageRepository
import com.herlianzhang.mikropos.utils.extensions.getImageDisplayName
import com.herlianzhang.mikropos.vo.CustomerDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CustomerDetailViewModel @AssistedInject constructor(
    @Assisted
    val id: Int,
    private val CustomerRepository: CustomerRepository,
    private val imageRepository: ImageRepository,
    app: Application
): AndroidViewModel(app) {
    private var updateJob: Job? = null
    private var uploadJob: Job? = null

    private val _data = MutableStateFlow<CustomerDetail?>(null)
    val data: StateFlow<CustomerDetail?>
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

    private val _event = Channel<CustomerDetailEvent>()
    val event: Flow<CustomerDetailEvent>
        get() = _event.receiveAsFlow()

    init {
        getCustomer()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getCustomer()
        }
    }

    private fun getCustomer() {
        viewModelScope.launch {
            CustomerRepository.getCustomer(id).collect { result ->
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

    fun updateCustomer(params: Map<String, Any>, fromDialog: Boolean = true) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            CustomerRepository.updateCustomer(id, params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> {
                        if (fromDialog)
                            _isDialogLoading.emit(result.state.value)
                        else
                            _isUploadingImage.emit(result.state.value)
                    }
                    is ApiResult.Success -> {
                        _event.send(CustomerDetailEvent.SetHasChanges)
                        _event.send(CustomerDetailEvent.HideDialog)
                        _data.emit(result.data)
                    }
                    is ApiResult.Failed -> _event.send(CustomerDetailEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }

    fun cancelUpdateCustomer() {
        viewModelScope.launch {
            _isDialogLoading.emit(false)
            updateJob?.cancel()
        }
    }

    fun deleteCustomer() {
        viewModelScope.launch {
            CustomerRepository.deleteCustomer(id).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> {
                        _event.send(CustomerDetailEvent.SetHasChanges)
                        _event.send(CustomerDetailEvent.Back)
                    }
                    is ApiResult.Failed -> _event.send(CustomerDetailEvent.ShowErrorSnackbar(result.message))
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
                    is ApiResult.Failed -> _event.send(CustomerDetailEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> {
                        result.data?.url?.let { url ->
                            val params = mapOf("photo" to url)
                            updateCustomer(params, false)
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): CustomerDetailViewModel
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
        fun getViewModel(id: Int): CustomerDetailViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).customerDetailViewModelFactory()
            return viewModel(factory = providesFactory(factory, id))
        }
    }
}