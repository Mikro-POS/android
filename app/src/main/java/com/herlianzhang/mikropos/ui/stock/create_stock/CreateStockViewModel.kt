package com.herlianzhang.mikropos.ui.stock.create_stock

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.herlianzhang.mikropos.MainActivity
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.StockRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateStockViewModel @AssistedInject constructor(
    @Assisted
    private val productId: Int,
    private val stockRepository: StockRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<CreateStockEvent>()
    val event: Flow<CreateStockEvent>
        get() = _event.receiveAsFlow()

    init {
        Timber.d("masuk productId $productId")
    }

    fun createStock(
        supplierName: String,
        amount: Long,
        price: Long,
        isRefund: Boolean
    ) {
        val params = mutableMapOf<String, Any>()
        params["supplier_name"] = supplierName
        params["amount"] = amount
        params["purchase_price"] = price
        params["source"] = if (isRefund) "CUSTOMER" else "SUPPLIER"
        viewModelScope.launch {
            stockRepository.createStock(productId, params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateStockEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> _event.send(CreateStockEvent.BackWithResult)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(productId: Int): CreateStockViewModel
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
        fun getViewModel(productId: Int):CreateStockViewModel {
            val factory = EntryPointAccessors.fromActivity(
                LocalContext.current as Activity,
                MainActivity.ViewModelFactoryProvider::class.java
            ).createStockViewModelFactory()
            return viewModel(factory = providesFactory(factory, productId))
        }
    }
}