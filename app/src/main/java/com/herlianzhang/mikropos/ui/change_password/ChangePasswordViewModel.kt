package com.herlianzhang.mikropos.ui.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.UserRepository
import com.herlianzhang.mikropos.ui.expense.create_expense_category.CreateExpenseCategoryEvent
import com.herlianzhang.mikropos.vo.ChangePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<ChangePasswordEvent>()
    val event: Flow<ChangePasswordEvent>
        get() = _event.receiveAsFlow()

    fun changePassword(data: ChangePassword) {
        viewModelScope.launch {
            userRepository.changePassword(data).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(ChangePasswordEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> {
                        _event.send(ChangePasswordEvent.ShowErrorSnackbar("Password berhasil diubah"))
                        delay(500)
                        _event.send(ChangePasswordEvent.ClearUserInput)
                    }
                }
            }
        }
    }
}