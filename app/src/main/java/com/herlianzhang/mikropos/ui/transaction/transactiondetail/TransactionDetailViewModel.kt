package com.herlianzhang.mikropos.ui.transaction.transactiondetail

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herlianzhang.mikropos.MainActivity
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.TransactionRepository
import com.herlianzhang.mikropos.vo.PayInstallments
import com.herlianzhang.mikropos.vo.TransactionDetail
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

class TransactionDetailViewModel @AssistedInject constructor(
    @Assisted
    val id: Int,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private var job: Job? = null

    private val _data = MutableStateFlow<TransactionDetail?>(null)
    val data: StateFlow<TransactionDetail?>
        get() = _data

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isDialogLoading = MutableStateFlow(false)
    val isDialogLoading: StateFlow<Boolean>
        get() = _isDialogLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    private val _isNotFound = MutableStateFlow(false)
    val isNotFound: StateFlow<Boolean>
        get() = _isNotFound

    private val _event = Channel<TransactionDetailEvent>()
    val event: Flow<TransactionDetailEvent>
        get() = _event.receiveAsFlow()

    init {
        getTransaction()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getTransaction()
        }
    }

    fun payInstallments(value: String) {
        value.toLongOrNull()?.let { amount ->
            job?.cancel()
            job = viewModelScope.launch {
                transactionRepository.payInstallments(id, PayInstallments(amount)).collect { result ->
                    when(result) {
                        is ApiResult.Success -> {
                            _event.send(TransactionDetailEvent.HideDialog)
                            _data.emit(result.data)
                        }
                        is ApiResult.Loading -> _isDialogLoading.emit(result.state.value)
                        is ApiResult.Failed -> _event.send(TransactionDetailEvent.ShowErrorSnackbar(result.message))
                    }
                }
            }
        }
    }

    fun cancelPayInstallments() {
        viewModelScope.launch {
            _isDialogLoading.emit(false)
            job?.cancel()
        }
    }

    private fun getTransaction() {
        viewModelScope.launch {
            transactionRepository.getTransaction(id).collect { result ->
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

    @AssistedFactory
    interface Factory {
        fun create(id: Int): TransactionDetailViewModel
    }

    companion object {
        private fun providesFactory(
            assistedFactory: Factory,
            transactionId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(transactionId) as T
            }
        }

        @Composable
        fun getViewModel(id: Int): TransactionDetailViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).transactionDetailViewModelFactory()
            return viewModel(factory = providesFactory(factory, id))
        }
    }
}