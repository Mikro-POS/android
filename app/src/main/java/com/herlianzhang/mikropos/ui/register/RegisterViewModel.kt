package com.herlianzhang.mikropos.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.UserRepository
import com.herlianzhang.mikropos.utils.UserPreferences
import com.herlianzhang.mikropos.vo.Register
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPref: UserPreferences
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _event = Channel<RegisterEvent>()
    val event: Flow<RegisterEvent>
        get() = _event.receiveAsFlow()

    fun register(
        username: String,
        name: String,
        address: String,
        password: String
    ) {
        val data = Register(
            username = username,
            name = name,
            password = password,
            address = address.ifEmpty { null },
        )

        viewModelScope.launch {
            userRepository.register(data).collect { result ->
                when(result) {
                    is ApiResult.Success -> {
                        val accessToken = result.data?.accessToken ?: return@collect
                        userPref.accessToken = accessToken
                        userPref.user = result.data.user
                        _event.send(RegisterEvent.NavigateToHome)
                    }
                    is ApiResult.Failed -> _event.send(RegisterEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                }
            }
        }
    }
}